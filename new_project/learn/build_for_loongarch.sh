#!/bin/bash
# build_for_loongarch.sh - 龙芯平台构建脚本

echo "========================================="
echo "      龙芯平台构建脚本 v1.0"
echo "========================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查当前架构
echo -e "${YELLOW}检查系统架构...${NC}"
ARCH=$(uname -m)
echo "当前系统架构: $ARCH"

if [[ "$ARCH" == "loongarch64" ]]; then
    echo -e "${GREEN}✅ 运行在龙芯平台${NC}"
elif [[ "$ARCH" == "x86_64" ]]; then
    echo -e "${YELLOW}⚠️  运行在x86_64平台，将构建通用版本${NC}"
else
    echo -e "${RED}❌ 未知架构: $ARCH${NC}"
fi

# 检查Java版本
echo -e "${YELLOW}检查Java版本...${NC}"
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2)
    echo "Java版本: $JAVA_VERSION"
    
    # 提取主版本号
    MAJOR_VERSION=$(echo $JAVA_VERSION | cut -d'.' -f1)
    if [[ "$MAJOR_VERSION" -ge "17" ]]; then
        echo -e "${GREEN}✅ Java版本符合要求${NC}"
    else
        echo -e "${RED}❌ Java版本过低，需要Java 17+${NC}"
        exit 1
    fi
else
    echo -e "${RED}❌ 未找到Java，请先安装Java 17+${NC}"
    exit 1
fi

# 检查Maven
echo -e "${YELLOW}检查Maven...${NC}"
if command -v ./mvnw &> /dev/null; then
    echo -e "${GREEN}✅ 找到Maven Wrapper${NC}"
    MAVEN_CMD="./mvnw"
elif command -v mvn &> /dev/null; then
    echo -e "${GREEN}✅ 找到系统Maven${NC}"
    MAVEN_CMD="mvn"
else
    echo -e "${RED}❌ 未找到Maven，请先安装Maven${NC}"
    exit 1
fi

# 设置JVM参数（针对龙芯优化）
export MAVEN_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

# 清理项目
echo -e "${YELLOW}清理项目...${NC}"
$MAVEN_CMD clean
if [[ $? -ne 0 ]]; then
    echo -e "${RED}❌ 清理失败${NC}"
    exit 1
fi
echo -e "${GREEN}✅ 清理完成${NC}"

# 编译项目
echo -e "${YELLOW}编译项目...${NC}"
$MAVEN_CMD compile
if [[ $? -ne 0 ]]; then
    echo -e "${RED}❌ 编译失败${NC}"
    exit 1
fi
echo -e "${GREEN}✅ 编译完成${NC}"

# 运行测试
echo -e "${YELLOW}运行测试...${NC}"
$MAVEN_CMD test
if [[ $? -ne 0 ]]; then
    echo -e "${YELLOW}⚠️  测试失败，但继续构建...${NC}"
fi

# 打包应用
echo -e "${YELLOW}打包应用...${NC}"
$MAVEN_CMD package -DskipTests
if [[ $? -ne 0 ]]; then
    echo -e "${RED}❌ 打包失败${NC}"
    exit 1
fi
echo -e "${GREEN}✅ 打包完成${NC}"

# 显示构建结果
echo -e "${YELLOW}构建结果:${NC}"
ls -la target/

# 获取JAR文件信息
JAR_FILE=$(find target/ -name "*.jar" -not -name "*sources.jar" | head -1)
if [[ -f "$JAR_FILE" ]]; then
    JAR_SIZE=$(du -h "$JAR_FILE" | cut -f1)
    echo -e "${GREEN}📦 JAR文件: $JAR_FILE (大小: $JAR_SIZE)${NC}"
    
    # 验证JAR文件
    echo -e "${YELLOW}验证JAR文件...${NC}"
    java -jar "$JAR_FILE" --version 2>/dev/null || echo "JAR文件创建成功"
    echo -e "${GREEN}✅ JAR文件验证通过${NC}"
else
    echo -e "${RED}❌ 未找到JAR文件${NC}"
    exit 1
fi

# 创建部署目录结构
echo -e "${YELLOW}创建部署目录结构...${NC}"
mkdir -p deploy/lib
mkdir -p deploy/config
mkdir -p deploy/logs
mkdir -p deploy/scripts

# 复制文件到部署目录
cp "$JAR_FILE" deploy/lib/
cp src/main/resources/application-loongarch.properties deploy/config/
cp database_schema.sql deploy/ 2>/dev/null || echo "数据库脚本不存在，跳过"

# 创建启动脚本
cat > deploy/scripts/start.sh << 'EOF'
#!/bin/bash
# 龙芯平台启动脚本

APP_HOME="$(cd "$(dirname "$0")/.." && pwd)"
APP_JAR=$(find "$APP_HOME/lib" -name "*.jar" | head -1)
PID_FILE="$APP_HOME/app.pid"
LOG_FILE="$APP_HOME/logs/application.log"

# 创建日志目录
mkdir -p "$APP_HOME/logs"

# JVM参数（针对龙芯优化）
JAVA_OPTS="-server \
-Xms2g \
-Xmx4g \
-XX:+UseG1GC \
-XX:MaxGCPauseMillis=200 \
-XX:+UseCompressedOops \
-XX:+UseStringDeduplication \
-Djava.awt.headless=true \
-Dfile.encoding=UTF-8 \
-Duser.timezone=Asia/Shanghai \
-Dspring.profiles.active=loongarch \
-Dspring.config.additional-location=file:$APP_HOME/config/"

echo "启动学习平台..."
echo "应用路径: $APP_JAR"
echo "配置目录: $APP_HOME/config"
echo "日志文件: $LOG_FILE"

# 检查是否已经运行
if [[ -f "$PID_FILE" ]]; then
    PID=$(cat "$PID_FILE")
    if ps -p $PID > /dev/null 2>&1; then
        echo "应用已经在运行 (PID: $PID)"
        exit 1
    else
        rm -f "$PID_FILE"
    fi
fi

# 启动应用
cd "$APP_HOME"
nohup java $JAVA_OPTS -jar "$APP_JAR" > "$LOG_FILE" 2>&1 &
PID=$!
echo $PID > "$PID_FILE"

echo "应用已启动，PID: $PID"
echo "查看日志: tail -f $LOG_FILE"
EOF

# 创建停止脚本
cat > deploy/scripts/stop.sh << 'EOF'
#!/bin/bash
# 停止脚本

APP_HOME="$(cd "$(dirname "$0")/.." && pwd)"
PID_FILE="$APP_HOME/app.pid"

if [[ -f "$PID_FILE" ]]; then
    PID=$(cat "$PID_FILE")
    if ps -p $PID > /dev/null 2>&1; then
        echo "停止应用 (PID: $PID)..."
        kill $PID
        
        # 等待进程结束
        for i in {1..10}; do
            if ! ps -p $PID > /dev/null 2>&1; then
                echo "应用已停止"
                rm -f "$PID_FILE"
                exit 0
            fi
            sleep 1
        done
        
        # 强制杀死
        echo "强制停止应用..."
        kill -9 $PID
        rm -f "$PID_FILE"
        echo "应用已强制停止"
    else
        echo "应用未运行"
        rm -f "$PID_FILE"
    fi
else
    echo "PID文件不存在，应用可能未运行"
fi
EOF

# 创建状态检查脚本
cat > deploy/scripts/status.sh << 'EOF'
#!/bin/bash
# 状态检查脚本

APP_HOME="$(cd "$(dirname "$0")/.." && pwd)"
PID_FILE="$APP_HOME/app.pid"

echo "========== 应用状态 =========="
if [[ -f "$PID_FILE" ]]; then
    PID=$(cat "$PID_FILE")
    if ps -p $PID > /dev/null 2>&1; then
        echo "✅ 应用正在运行 (PID: $PID)"
        
        # 检查端口
        if netstat -tlnp 2>/dev/null | grep ":8080.*$PID/" > /dev/null; then
            echo "✅ 端口8080已监听"
        else
            echo "⚠️  端口8080未监听"
        fi
        
        # 检查内存使用
        MEMORY=$(ps -p $PID -o %mem= | tr -d ' ')
        echo "📊 内存使用: ${MEMORY}%"
        
        # 检查CPU使用
        CPU=$(ps -p $PID -o %cpu= | tr -d ' ')
        echo "📊 CPU使用: ${CPU}%"
        
    else
        echo "❌ 应用未运行（PID文件存在但进程不存在）"
        rm -f "$PID_FILE"
    fi
else
    echo "❌ 应用未运行（PID文件不存在）"
fi

echo ""
echo "========== 系统信息 =========="
echo "CPU架构: $(uname -m)"
echo "系统负载: $(uptime | awk -F'load average:' '{print $2}')"
echo "内存使用: $(free -h | grep '^Mem:' | awk '{print $3"/"$2}')"
echo "磁盘使用: $(df -h . | tail -1 | awk '{print $3"/"$2" ("$5")"}')"
EOF

# 设置脚本执行权限
chmod +x deploy/scripts/*.sh

echo -e "${GREEN}✅ 构建完成！${NC}"
echo ""
echo "📁 部署文件已创建在 ./deploy/ 目录："
echo "   lib/          - JAR文件"
echo "   config/       - 配置文件"
echo "   logs/         - 日志目录"
echo "   scripts/      - 启动脚本"
echo ""
echo "🚀 部署命令："
echo "   启动: ./deploy/scripts/start.sh"
echo "   停止: ./deploy/scripts/stop.sh"
echo "   状态: ./deploy/scripts/status.sh"
echo ""
echo "📋 配置文件: ./deploy/config/application-loongarch.properties" 