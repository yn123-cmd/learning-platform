# 注册功能调试指南

## 当前问题
收到400错误，需要调试注册接口

## 调试步骤

### 1. 确保服务器正在运行
```bash
./mvnw spring-boot:run
```

### 2. 测试注册接口（命令行）
```bash
# 基本注册测试
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser123",
    "email": "testuser123@example.com",
    "password": "123456",
    "nickName": "测试用户123"
  }' -v

# 查看详细响应
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser2024",
    "email": "newuser2024@example.com", 
    "password": "password123",
    "nickName": "新用户2024"
  }' -w "\nHTTP Status: %{http_code}\n" -v
```

### 3. PowerShell测试（Windows）
```powershell
# 创建测试数据
$registerData = @{
    username = "testuser456"
    email = "testuser456@example.com"
    password = "123456"
    nickName = "测试用户456"
} | ConvertTo-Json -Depth 10

# 发送请求
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method Post -Body $registerData -ContentType "application/json" -Verbose
    Write-Host "注册成功:" -ForegroundColor Green
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "注册失败:" -ForegroundColor Red
    Write-Host $_.Exception.Message
    if ($_.Exception.Response) {
        $reader = [System.IO.StreamReader]::new($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "错误详情:" $responseBody
    }
}
```

### 4. 检查服务器日志
启动应用后，观察控制台输出，特别关注：
- "收到注册请求:" 日志
- "参数验证失败:" 错误信息
- "注册失败:" 错误详情
- 数据库连接错误

### 5. 可能的问题和解决方案

#### 问题1: 数据库连接失败
**症状**: 看到数据库连接错误
**解决**: 确保MySQL正在运行并且数据库已创建
```bash
# 创建数据库
mysql -u root -p < database_schema.sql
```

#### 问题2: 字段验证失败
**症状**: 看到"参数验证失败"错误
**解决**: 检查前端发送的数据格式
- username: 3-20字符
- email: 有效邮箱格式
- password: 6-20字符
- nickName: 2-10字符

#### 问题3: 用户名或邮箱已存在
**症状**: "用户名已存在"或"邮箱已存在"
**解决**: 使用不同的用户名和邮箱

#### 问题4: JWT密钥问题
**症状**: JWT相关错误
**解决**: 确保JWT密钥长度足够（已修复）

### 6. 数据库查看命令
```sql
-- 查看用户表结构
DESCRIBE users;

-- 查看现有用户
SELECT id, username, email, nick_name, role, status, create_time FROM users;

-- 删除测试用户（如果需要重新测试）
DELETE FROM users WHERE username LIKE 'test%';
```

### 7. 前端调试建议
如果是从前端发送请求，请检查：

1. **请求URL是否正确**
   ```javascript
   // 确保URL正确
   const url = 'http://localhost:8080/api/auth/register';
   ```

2. **请求头设置**
   ```javascript
   headers: {
     'Content-Type': 'application/json',
   }
   ```

3. **数据格式**
   ```javascript
   const data = {
     username: 'testuser',      // 3-20字符
     email: 'test@example.com', // 有效邮箱
     password: '123456',        // 6-20字符  
     nickName: '测试用户'       // 2-10字符
   };
   ```

4. **错误处理**
   ```javascript
   try {
     const response = await fetch(url, {
       method: 'POST',
       headers: { 'Content-Type': 'application/json' },
       body: JSON.stringify(data)
     });
     
     if (!response.ok) {
       const errorText = await response.text();
       console.error('HTTP错误:', response.status, errorText);
       return;
     }
     
     const result = await response.json();
     console.log('注册成功:', result);
   } catch (error) {
     console.error('请求失败:', error);
   }
   ```

### 8. 快速验证脚本
创建一个简单的HTML文件来测试：

```html
<!DOCTYPE html>
<html>
<head>
    <title>注册测试</title>
</head>
<body>
    <h1>注册测试</h1>
    <button onclick="testRegister()">测试注册</button>
    <div id="result"></div>

    <script>
        async function testRegister() {
            const testData = {
                username: 'testuser' + Date.now(),
                email: 'test' + Date.now() + '@example.com',
                password: '123456',
                nickName: '测试用户'
            };

            try {
                console.log('发送数据:', testData);
                
                const response = await fetch('http://localhost:8080/api/auth/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(testData)
                });

                console.log('响应状态:', response.status);
                
                const result = await response.text();
                console.log('响应内容:', result);
                
                document.getElementById('result').innerHTML = 
                    '<h3>状态: ' + response.status + '</h3>' +
                    '<pre>' + result + '</pre>';
                    
            } catch (error) {
                console.error('请求失败:', error);
                document.getElementById('result').innerHTML = 
                    '<h3 style="color:red">错误: ' + error.message + '</h3>';
            }
        }
    </script>
</body>
</html>
```

运行这个HTML文件，打开浏览器控制台查看详细的错误信息。 