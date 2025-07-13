# 智慧教学平台

基于Spring Boot + Vue.js的前后端分离学习平台，支持龙芯(LoongArch)平台部署。

## 项目简介

这是一个采用B/S架构开发的智慧教学平台，包含完整的前后端代码，支持在龙芯指令集(3C5000)服务器或工作站上部署运行。

## 技术栈

### 后端技术栈
- **Java 17+**
- **Spring Boot 3.5**
- **Spring Security** - 安全框架
- **Spring Data JPA** - 数据持久层
- **MySQL 8.0** - 数据库
- **JWT** - 身份验证
- **Maven** - 项目管理

### 前端技术栈
- **Vue.js 2.x**
- **Vue Router** - 路由管理
- **Vuex** - 状态管理
- **Element UI** - UI组件库
- **Axios** - HTTP客户端

## 项目结构

```
new_project/
├── front/              # 前端Vue项目
│   ├── src/
│   │   ├── components/ # 组件
│   │   ├── views/      # 页面
│   │   ├── router/     # 路由
│   │   └── store/      # 状态管理
│   └── package.json
├── learn/              # 后端Spring Boot项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/   # Java源码
│   │   │   └── resources/ # 配置文件
│   │   └── test/       # 测试代码
│   ├── pom.xml
│   └── 龙芯部署指南.md
└── README.md
```

## 龙芯平台特性

✅ **完全支持龙芯LoongArch架构**
✅ **针对龙芯CPU优化的JVM参数**
✅ **专用的龙芯平台配置文件**
✅ **自动化构建和部署脚本**
✅ **系统服务化部署支持**

## 快速开始

### 环境要求
- **CPU**: 龙芯3C5000及以上
- **内存**: 8GB以上
- **Java**: 17+（支持LoongArch）
- **MySQL**: 8.0+
- **Node.js**: 16+

### 1. 后端部署
```bash
cd learn/
# 环境检查
./pre_deploy_check.sh
# 构建部署
./build_for_loongarch.sh
# 启动应用
cd deploy/
./scripts/start.sh
```

### 2. 前端部署
```bash
cd front/
npm install
npm run build
# 部署到Nginx
```

### 3. 数据库配置
```sql
CREATE DATABASE learn_platform CHARACTER SET utf8mb4;
CREATE USER 'learn_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON learn_platform.* TO 'learn_user'@'localhost';
```

## 部署文档

- [龙芯部署指南](learn/龙芯部署指南.md)
- [龙芯部署总结](learn/龙芯部署总结.md)

## 功能特性

- 🎓 **课程管理** - 完整的课程创建和管理功能
- 👥 **用户管理** - 学生、教师角色管理
- 📝 **作业系统** - 作业发布、提交、批改
- 📊 **数据分析** - 学习数据统计和分析
- 🔐 **权限控制** - 基于角色的访问控制
- 📱 **响应式设计** - 支持多种设备访问

## API文档

启动后端服务后，可访问：
- Swagger UI: http://localhost:8080/swagger-ui.html
- API文档: http://localhost:8080/v3/api-docs

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目基于 MIT 许可证开源 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

如有问题，请提交 Issue 或联系开发团队。

---

**中国软件杯参赛项目 - 龙芯平台适配版本** 