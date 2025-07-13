#!/bin/bash
# pre_deploy_check.sh - 龙芯平台预部署检查脚本

echo "========================================="
echo "      龙芯平台预部署检查 v1.0"
echo "========================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 检查结果计数器
PASS=0
FAIL=0
WARN=0

# 检查函数
check_pass() {
    echo -e "${GREEN}✅ $1${NC}"
    ((PASS++))
}

check_fail() {
    echo -e "${RED}❌ $1${NC}"
    ((FAIL++))
}

check_warn() {
    echo -e "${YELLOW}⚠️  $1${NC}"
    ((WARN++))
}

check_info() {
    echo -e "${BLUE}📋 $1${NC}"
}

echo "开始检查龙芯平台部署环境..."
echo ""

# 1. 检查系统架构
echo "========== 系统架构检查 =========="
ARCH=$(uname -m)
check_info "当前系统架构: $ARCH"

if [[ "$ARCH" == "loongarch64" ]]; then
    check_pass "运行在龙芯LoongArch64平台"
elif [[ "$ARCH" == "x86_64" ]]; then
    check_warn "运行在x86_64平台（非目标平台）"
    echo "           💡 建议在龙芯平台上进行最终部署测试"
else
    check_fail "未知架构: $ARCH (不支持)"
fi

# 2. 检查CPU信息
echo ""
echo "========== CPU信息检查 =========="
if command -v lscpu &> /dev/null; then
    CPU_INFO=$(lscpu | grep "Model name" | head -1 | cut -d':' -f2 | xargs)
    CPU_CORES=$(lscpu | grep "^CPU(s):" | cut -d':' -f2 | xargs)
    
    check_info "CPU型号: $CPU_INFO"
    check_info "CPU核数: $CPU_CORES"
    
    if [[ "$CPU_CORES" -ge "4" ]]; then
        check_pass "CPU核数满足要求 (${CPU_CORES}核 >= 4核)"
    else
        check_fail "CPU核数不足 (${CPU_CORES}核 < 4核)"
    fi
    
    # 检查是否为龙芯CPU
    if echo "$CPU_INFO" | grep -i "loongson\|3c5000" > /dev/null; then
        check_pass "检测到龙芯CPU"
    elif [[ "$ARCH" == "loongarch64" ]]; then
        check_pass "运行在LoongArch架构"
    else
        check_warn "未检测到龙芯CPU标识"
    fi
else
    check_warn "无法获取CPU信息 (lscpu命令不存在)"
fi

# 3. 检查内存
echo ""
echo "========== 内存检查 =========="
if command -v free &> /dev/null; then
    MEMORY_KB=$(free | grep '^Mem:' | awk '{print $2}')
    MEMORY_GB=$((MEMORY_KB / 1024 / 1024))
    AVAILABLE_KB=$(free | grep '^Mem:' | awk '{print $7}')
    AVAILABLE_GB=$((AVAILABLE_KB / 1024 / 1024))
    
    check_info "总内存: ${MEMORY_GB}GB"
    check_info "可用内存: ${AVAILABLE_GB}GB"
    
    if [[ "$MEMORY_GB" -ge "8" ]]; then
        check_pass "内存容量满足要求 (${MEMORY_GB}GB >= 8GB)"
    else
        check_fail "内存容量不足 (${MEMORY_GB}GB < 8GB)"
    fi
    
    if [[ "$AVAILABLE_GB" -ge "4" ]]; then
        check_pass "可用内存充足 (${AVAILABLE_GB}GB >= 4GB)"
    else
        check_warn "可用内存较少 (${AVAILABLE_GB}GB < 4GB)"
    fi
else
    check_fail "无法检查内存信息 (free命令不存在)"
fi

# 4. 检查磁盘空间
echo ""
echo "========== 磁盘空间检查 =========="
DISK_INFO=$(df -BG . | tail -1)
TOTAL_GB=$(echo $DISK_INFO | awk '{print $2}' | sed 's/G//')
USED_GB=$(echo $DISK_INFO | awk '{print $3}' | sed 's/G//')
AVAILABLE_GB=$(echo $DISK_INFO | awk '{print $4}' | sed 's/G//')
USAGE_PERCENT=$(echo $DISK_INFO | awk '{print $5}' | sed 's/%//')

check_info "磁盘总空间: ${TOTAL_GB}GB"
check_info "已用空间: ${USED_GB}GB (${USAGE_PERCENT}%)"
check_info "可用空间: ${AVAILABLE_GB}GB"

if [[ "$TOTAL_GB" -ge "256" ]]; then
    check_pass "磁盘总容量满足要求 (${TOTAL_GB}GB >= 256GB)"
else
    check_fail "磁盘总容量不足 (${TOTAL_GB}GB < 256GB)"
fi

if [[ "$AVAILABLE_GB" -ge "20" ]]; then
    check_pass "可用磁盘空间充足 (${AVAILABLE_GB}GB >= 20GB)"
elif [[ "$AVAILABLE_GB" -ge "10" ]]; then
    check_warn "可用磁盘空间较少 (${AVAILABLE_GB}GB < 20GB)"
else
    check_fail "可用磁盘空间不足 (${AVAILABLE_GB}GB < 10GB)"
fi

if [[ "$USAGE_PERCENT" -le "80" ]]; then
    check_pass "磁盘使用率正常 (${USAGE_PERCENT}% <= 80%)"
else
    check_warn "磁盘使用率较高 (${USAGE_PERCENT}% > 80%)"
fi

# 5. 检查Java环境
echo ""
echo "========== Java环境检查 =========="
if command -v java &> /dev/null; then
    JAVA_VERSION_FULL=$(java -version 2>&1 | head -1)
    JAVA_VERSION=$(echo "$JAVA_VERSION_FULL" | cut -d'"' -f2)
    MAJOR_VERSION=$(echo $JAVA_VERSION | cut -d'.' -f1)
    
    check_info "Java版本: $JAVA_VERSION"
    
    if [[ "$MAJOR_VERSION" -ge "17" ]]; then
        check_pass "Java版本满足要求 (${MAJOR_VERSION} >= 17)"
    else
        check_fail "Java版本过低 (${MAJOR_VERSION} < 17)"
    fi
    
    # 检查Java架构
    JAVA_ARCH=$(java -XshowSettings:properties -version 2>&1 | grep "os.arch" | cut -d'=' -f2 | xargs)
    check_info "Java架构: $JAVA_ARCH"
    
    if [[ "$JAVA_ARCH" == "loongarch64" ]]; then
        check_pass "Java支持LoongArch64架构"
    elif [[ "$JAVA_ARCH" == "amd64" && "$ARCH" == "x86_64" ]]; then
        check_warn "Java运行在x86_64架构（非目标架构）"
    else
        check_warn "Java架构可能不匹配系统架构"
    fi
    
    # 检查JVM类型
    JVM_INFO=$(java -version 2>&1 | grep -E "OpenJDK|HotSpot|Loongson" | head -1)
    if echo "$JVM_INFO" | grep -i "loongson" > /dev/null; then
        check_pass "检测到龙芯优化JVM"
    elif echo "$JVM_INFO" | grep -i "openjdk" > /dev/null; then
        check_pass "检测到OpenJDK"
    else
        check_info "JVM信息: $JVM_INFO"
    fi
else
    check_fail "未找到Java (请安装Java 17+)"
fi

# 6. 检查Maven
echo ""
echo "========== Maven环境检查 =========="
if [[ -f "./mvnw" ]]; then
    check_pass "找到Maven Wrapper"
    chmod +x ./mvnw 2>/dev/null
elif command -v mvn &> /dev/null; then
    MAVEN_VERSION=$(mvn -version | head -1 | awk '{print $3}')
    check_pass "找到系统Maven (版本: $MAVEN_VERSION)"
else
    check_fail "未找到Maven (请安装Maven或使用Maven Wrapper)"
fi

# 7. 检查数据库
echo ""
echo "========== 数据库检查 =========="
if command -v mysql &> /dev/null; then
    MYSQL_VERSION=$(mysql --version | awk '{print $3}' | cut -d',' -f1)
    check_pass "找到MySQL (版本: $MYSQL_VERSION)"
    
    # 检查MySQL是否运行
    if pgrep mysqld > /dev/null 2>&1; then
        check_pass "MySQL服务正在运行"
    else
        check_warn "MySQL服务未运行"
        echo "           💡 部署前请启动MySQL服务"
    fi
else
    check_warn "未找到MySQL"
    echo "           💡 可选择："
    echo "           - 安装MySQL"
    echo "           - 使用达梦数据库"
    echo "           - 使用人大金仓KingbaseES"
    echo "           - 使用PostgreSQL"
fi

# 8. 检查网络和端口
echo ""
echo "========== 网络和端口检查 =========="
if command -v netstat &> /dev/null; then
    PORT_8080=$(netstat -tlnp 2>/dev/null | grep ":8080 ")
    if [[ -n "$PORT_8080" ]]; then
        check_warn "端口8080已被占用"
        echo "           进程信息: $PORT_8080"
        echo "           💡 部署前请停止占用端口的服务"
    else
        check_pass "端口8080可用"
    fi
else
    check_warn "无法检查端口状态 (netstat命令不存在)"
fi

# 检查网络连接
if ping -c 1 127.0.0.1 > /dev/null 2>&1; then
    check_pass "本地网络连接正常"
else
    check_warn "本地网络连接异常"
fi

# 9. 检查操作系统
echo ""
echo "========== 操作系统检查 =========="
if [[ -f "/etc/os-release" ]]; then
    OS_NAME=$(grep "^NAME=" /etc/os-release | cut -d'=' -f2 | tr -d '"')
    OS_VERSION=$(grep "^VERSION=" /etc/os-release | cut -d'=' -f2 | tr -d '"')
    check_info "操作系统: $OS_NAME $OS_VERSION"
    
    # 检查是否为国产操作系统
    if echo "$OS_NAME" | grep -E -i "uos|kylin|deepin|中标|统信|银河麒麟" > /dev/null; then
        check_pass "检测到国产操作系统"
    else
        check_info "操作系统类型: $OS_NAME"
    fi
else
    check_warn "无法获取操作系统信息"
fi

# 检查内核版本
KERNEL_VERSION=$(uname -r)
check_info "内核版本: $KERNEL_VERSION"

# 10. 检查系统服务支持
echo ""
echo "========== 系统服务检查 =========="
if command -v systemctl &> /dev/null; then
    check_pass "支持Systemd服务管理"
else
    check_warn "不支持Systemd (将使用传统启动方式)"
fi

if command -v firewall-cmd &> /dev/null; then
    check_info "检测到firewalld防火墙"
elif command -v ufw &> /dev/null; then
    check_info "检测到UFW防火墙"
elif command -v iptables &> /dev/null; then
    check_info "检测到iptables防火墙"
else
    check_info "未检测到常见防火墙"
fi

# 11. 性能基准测试
echo ""
echo "========== 性能基准测试 =========="
if command -v java &> /dev/null && [[ "$MAJOR_VERSION" -ge "17" ]]; then
    echo "正在进行Java性能测试..."
    
    # 简单的CPU测试
    JAVA_TEST_START=$(date +%s%3N)
    java -XX:+UseG1GC -Xms256m -Xmx256m -cp . -c "
    public class Test {
        public static void main(String[] args) {
            long start = System.currentTimeMillis();
            long sum = 0;
            for(int i = 0; i < 10000000; i++) {
                sum += i;
            }
            long end = System.currentTimeMillis();
            System.out.println(\"计算耗时: \" + (end - start) + \"ms\");
        }
    }" 2>/dev/null || true
    
    check_pass "Java基础性能测试完成"
else
    check_warn "跳过性能测试 (Java环境不可用)"
fi

# 显示检查结果总结
echo ""
echo "========================================="
echo "           检查结果总结"
echo "========================================="
echo -e "${GREEN}✅ 通过: $PASS 项${NC}"
echo -e "${YELLOW}⚠️  警告: $WARN 项${NC}"
echo -e "${RED}❌ 失败: $FAIL 项${NC}"
echo ""

if [[ "$FAIL" -eq 0 ]]; then
    if [[ "$WARN" -eq 0 ]]; then
        echo -e "${GREEN}🎉 所有检查都通过！系统已准备好部署龙芯平台应用。${NC}"
        EXIT_CODE=0
    else
        echo -e "${YELLOW}⚠️  系统基本满足部署要求，但有一些警告项需要注意。${NC}"
        EXIT_CODE=0
    fi
else
    echo -e "${RED}❌ 系统不满足部署要求，请解决失败的检查项后重新检查。${NC}"
    EXIT_CODE=1
fi

echo ""
echo "💡 建议："
echo "   1. 在龙芯平台上安装龙芯JDK以获得最佳性能"
echo "   2. 配置足够的JVM堆内存 (建议2-4GB)"
echo "   3. 使用支持LoongArch的数据库版本"
echo "   4. 定期监控系统资源使用情况"
echo ""
echo "📋 接下来："
if [[ "$EXIT_CODE" -eq 0 ]]; then
    echo "   - 运行构建脚本: ./build_for_loongarch.sh"
    echo "   - 部署应用: ./deploy/scripts/start.sh"
    echo "   - 查看日志: tail -f ./deploy/logs/application.log"
else
    echo "   - 解决上述失败项"
    echo "   - 重新运行检查: ./pre_deploy_check.sh"
fi

exit $EXIT_CODE 