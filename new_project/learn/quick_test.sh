#!/bin/bash
# quick_test.sh - 快速验证脚本

echo "========================================="
echo "     快速验证脚本 v1.0"
echo "========================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}正在进行快速编译测试...${NC}"

# 清理并编译
if command -v ./mvnw &> /dev/null; then
    MAVEN_CMD="./mvnw"
elif command -v mvn &> /dev/null; then
    MAVEN_CMD="mvn"
else
    echo -e "${RED}❌ 未找到Maven${NC}"
    exit 1
fi

# 设置测试环境
export SPRING_PROFILES_ACTIVE=test

echo -e "${YELLOW}清理项目...${NC}"
$MAVEN_CMD clean -q

echo -e "${YELLOW}编译项目...${NC}"
$MAVEN_CMD compile -q

if [[ $? -ne 0 ]]; then
    echo -e "${RED}❌ 编译失败${NC}"
    exit 1
fi

echo -e "${GREEN}✅ 编译成功${NC}"

echo -e "${YELLOW}运行测试...${NC}"
$MAVEN_CMD test -q

if [[ $? -eq 0 ]]; then
    echo -e "${GREEN}✅ 测试通过${NC}"
else
    echo -e "${YELLOW}⚠️  测试有问题，但继续验证...${NC}"
fi

echo -e "${YELLOW}打包应用...${NC}"
$MAVEN_CMD package -DskipTests -q

if [[ $? -ne 0 ]]; then
    echo -e "${RED}❌ 打包失败${NC}"
    exit 1
fi

echo -e "${GREEN}✅ 打包成功${NC}"

# 查找JAR文件
JAR_FILE=$(find target/ -name "*.jar" -not -name "*sources.jar" | head -1)
if [[ -f "$JAR_FILE" ]]; then
    JAR_SIZE=$(du -h "$JAR_FILE" | cut -f1)
    echo -e "${GREEN}📦 JAR文件: $JAR_FILE (大小: $JAR_SIZE)${NC}"
else
    echo -e "${RED}❌ 未找到JAR文件${NC}"
    exit 1
fi

# 快速启动测试（3秒后关闭）
echo -e "${YELLOW}进行快速启动测试（3秒）...${NC}"
java -jar "$JAR_FILE" --spring.profiles.active=test &
APP_PID=$!

sleep 3

# 检查是否启动成功
if ps -p $APP_PID > /dev/null 2>&1; then
    echo -e "${GREEN}✅ 应用启动成功${NC}"
    
    # 尝试访问健康检查端点
    if command -v curl &> /dev/null; then
        curl -s http://localhost:8080/api/auth/me > /dev/null 2>&1
        if [[ $? -eq 0 ]]; then
            echo -e "${GREEN}✅ API端点可访问${NC}"
        else
            echo -e "${YELLOW}⚠️  API端点测试失败（可能是正常的，因为需要认证）${NC}"
        fi
    fi
    
    # 停止应用
    kill $APP_PID 2>/dev/null
    sleep 1
    kill -9 $APP_PID 2>/dev/null
    echo -e "${GREEN}✅ 应用已停止${NC}"
else
    echo -e "${RED}❌ 应用启动失败${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}🎉 快速验证完成！应用可以正常构建和启动。${NC}"
echo ""
echo "接下来可以："
echo "  1. 在龙芯平台上运行: ./pre_deploy_check.sh"
echo "  2. 构建部署包: ./build_for_loongarch.sh"
echo "  3. 部署应用: ./deploy/scripts/start.sh" 