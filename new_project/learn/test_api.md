# API测试文档

## 快速测试命令

### 1. 健康检查
```bash
curl -X GET "http://localhost:8080/api/test/health"
```

### 2. 用户登录测试
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'
```

### 3. 用户注册测试
```bash
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "123456",
    "nickName": "新用户"
  }'
```

### 4. 获取用户信息（需要先登录获取token）
```bash
# 先登录获取token
TOKEN=$(curl -s -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "123456"}' | \
  grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# 使用token获取用户信息
curl -X GET "http://localhost:8080/api/auth/me" \
  -H "Authorization: Bearer $TOKEN"
```

### 5. 获取用户列表
```bash
curl -X GET "http://localhost:8080/api/users"
```

### 6. 检查用户名是否存在
```bash
curl -X GET "http://localhost:8080/api/users/check-username?username=admin"
```

### 7. 检查邮箱是否存在
```bash
curl -X GET "http://localhost:8080/api/users/check-email?email=admin@example.com"
```

## PowerShell测试命令（Windows）

### 1. 健康检查
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/test/health" -Method Get
```

### 2. 用户登录
```powershell
$loginData = @{
    username = "admin"
    password = "123456"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -Body $loginData -ContentType "application/json"
```

### 3. 用户注册
```powershell
$registerData = @{
    username = "newuser"
    email = "newuser@example.com"
    password = "123456"
    nickName = "新用户"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method Post -Body $registerData -ContentType "application/json"
```

### 4. 获取用户信息
```powershell
# 先登录获取token
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -Body $loginData -ContentType "application/json"
$token = $loginResponse.data.token

# 使用token获取用户信息
$headers = @{
    Authorization = "Bearer $token"
}
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/me" -Method Get -Headers $headers
```

## JavaScript测试代码（浏览器控制台）

```javascript
// 1. 登录测试
async function testLogin() {
    const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            username: 'admin',
            password: '123456'
        })
    });
    
    const result = await response.json();
    console.log('登录结果:', result);
    
    if (result.code === 200) {
        // 保存token
        localStorage.setItem('token', result.data.token);
        console.log('Token已保存:', result.data.token);
    }
    
    return result;
}

// 2. 获取用户信息测试
async function testGetUserInfo() {
    const token = localStorage.getItem('token');
    if (!token) {
        console.log('请先登录');
        return;
    }
    
    const response = await fetch('http://localhost:8080/api/auth/me', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    
    const result = await response.json();
    console.log('用户信息:', result);
    return result;
}

// 3. 注册测试
async function testRegister() {
    const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            username: 'testuser2',
            email: 'testuser2@example.com',
            password: '123456',
            nickName: '测试用户2'
        })
    });
    
    const result = await response.json();
    console.log('注册结果:', result);
    return result;
}

// 4. 获取用户列表
async function testGetUsers() {
    const response = await fetch('http://localhost:8080/api/users');
    const result = await response.json();
    console.log('用户列表:', result);
    return result;
}

// 运行所有测试
async function runAllTests() {
    console.log('=== 开始API测试 ===');
    
    // 测试登录
    console.log('\n1. 测试登录');
    await testLogin();
    
    // 测试获取用户信息
    console.log('\n2. 测试获取用户信息');
    await testGetUserInfo();
    
    // 测试获取用户列表
    console.log('\n3. 测试获取用户列表');
    await testGetUsers();
    
    // 测试注册
    console.log('\n4. 测试注册');
    await testRegister();
    
    console.log('\n=== 测试完成 ===');
}

// 在浏览器控制台中运行：runAllTests()
```

## 预期响应格式

### 成功登录响应
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "nickName": "管理员",
      "status": 1,
      "createTime": "2024-01-01T10:00:00",
      "lastLoginTime": "2024-01-01T10:00:00"
    },
    "expiresIn": 86400
  },
  "timestamp": 1749026854951
}
```

### 错误响应
```json
{
  "code": 500,
  "message": "登录失败: 用户不存在",
  "data": null,
  "timestamp": 1749026854951
}
```

## 故障排除

1. **Connection refused**
   - 确保应用已启动：`./mvnw spring-boot:run`
   - 检查端口8080是否被占用

2. **401 Unauthorized**
   - 检查token是否正确
   - 确认token没有过期

3. **500 Internal Server Error**
   - 查看应用日志
   - 检查数据库连接 