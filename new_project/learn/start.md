# 学习平台启动说明

## 解决MySQL数据库不存在的问题

### 方法1：自动创建数据库（推荐）
我已经在配置文件中添加了 `createDatabaseIfNotExist=true` 参数，MySQL会自动创建数据库。

### 方法2：手动创建数据库
如果方法1不生效，请手动创建数据库：

```sql
-- 1. 登录MySQL
mysql -u root -p

-- 2. 创建数据库
CREATE DATABASE learn_platform DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;

-- 3. 退出MySQL
exit;
```

### 方法3：使用提供的初始化脚本
```bash
# 在项目根目录执行
mysql -u root -p < create_database.sql
```

### 方法4：使用H2数据库（快速测试）
如果您想快速启动项目进行测试，可以修改 `application.properties`：

1. 注释掉MySQL配置
2. 取消注释H2配置

```properties
# 使用H2数据库（内存数据库，适合开发测试）
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# 注释掉MySQL配置
# spring.datasource.url=jdbc:mysql://localhost:3306/learn_platform?createDatabaseIfNotExist=true
# spring.datasource.username=root
# spring.datasource.password=root
# spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
# spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

## 启动项目

1. **确保MySQL服务运行**
   ```bash
   # Windows
   net start mysql
   
   # macOS (使用Homebrew)
   brew services start mysql
   
   # Linux
   sudo systemctl start mysql
   ```

2. **启动Spring Boot应用**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **测试API**
   ```bash
   # 健康检查
   curl http://localhost:8080/api/test/health
   
   # 用户登录
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"123456"}'
   ```

## 验证数据库连接

### 如果使用MySQL：
- 访问 phpMyAdmin 或 MySQL Workbench
- 查看 `learn_platform` 数据库中的 `users` 表

### 如果使用H2：
- 启动应用后访问：http://localhost:8080/api/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## 默认测试账号

- **管理员**: 用户名 `admin`, 密码 `123456`
- **测试用户**: 用户名 `testuser`, 密码 `123456`

## 常见问题

1. **Access denied for user 'root'@'localhost'**
   - 检查MySQL用户名和密码是否正确
   - 修改 `application.properties` 中的数据库连接信息

2. **Could not create connection to database server**
   - 确保MySQL服务正在运行
   - 检查端口3306是否被占用

3. **Unknown database 'learn_platform'**
   - 按照上述方法创建数据库
   - 或使用H2数据库进行快速测试 