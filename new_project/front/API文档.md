# 学习平台API接口文档

## 基础信息

- **基础URL**: `http://localhost:8080/api`
- **认证方式**: JWT Token (Bearer Token)
- **响应格式**: JSON

## 响应格式说明

所有接口统一返回格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1634567890123
}
```

## 认证相关接口

### 1. 用户登录

**接口地址**: `POST /auth/login`

**请求参数**:
```json
{
  "username": "admin",
  "password": "123456"
}
```

**响应示例**:
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
  "timestamp": 1634567890123
}
```

### 2. 用户注册

**接口地址**: `POST /auth/register`

**请求参数**:
```json
{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "123456",
  "nickName": "新用户"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "user": {
      "id": 3,
      "username": "newuser",
      "email": "newuser@example.com",
      "nickName": "新用户",
      "status": 1,
      "createTime": "2024-01-01T10:00:00"
    },
    "expiresIn": 86400
  },
  "timestamp": 1634567890123
}
```

### 3. 获取当前用户信息

**接口地址**: `GET /auth/me`

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**响应示例**:
```json
{
  "code": 200,
  "message": "获取用户信息成功",
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "nickName": "管理员",
    "status": 1,
    "createTime": "2024-01-01T10:00:00",
    "lastLoginTime": "2024-01-01T10:00:00"
  },
  "timestamp": 1634567890123
}
```

### 4. 验证Token

**接口地址**: `POST /auth/validate`

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**响应示例**:
```json
{
  "code": 200,
  "message": "Token验证完成",
  "data": {
    "valid": true,
    "username": "admin",
    "userId": 1
  },
  "timestamp": 1634567890123
}
```

## 用户管理接口

### 1. 获取用户列表

**接口地址**: `GET /users`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "nickName": "管理员",
      "status": 1,
      "createTime": "2024-01-01T10:00:00",
      "lastLoginTime": "2024-01-01T10:00:00"
    },
    {
      "id": 2,
      "username": "testuser",
      "email": "test@example.com",
      "nickName": "测试用户",
      "status": 1,
      "createTime": "2024-01-01T10:00:00"
    }
  ],
  "timestamp": 1634567890123
}
```

### 2. 根据ID获取用户

**接口地址**: `GET /users/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "nickName": "管理员",
    "status": 1,
    "createTime": "2024-01-01T10:00:00",
    "lastLoginTime": "2024-01-01T10:00:00"
  },
  "timestamp": 1634567890123
}
```

### 3. 根据用户名获取用户

**接口地址**: `GET /users/username/{username}`

**响应示例**: 同上

### 4. 检查用户名是否存在

**接口地址**: `GET /users/check-username?username=admin`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true,
  "timestamp": 1634567890123
}
```

### 5. 检查邮箱是否存在

**接口地址**: `GET /users/check-email?email=admin@example.com`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true,
  "timestamp": 1634567890123
}
```

## 测试接口

### 1. 健康检查

**接口地址**: `GET /test/health`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "服务正常运行",
  "timestamp": 1634567890123
}
```

### 2. 系统信息

**接口地址**: `GET /test/info`

**响应示例**:
```json
{
  "code": 200,
  "message": "系统信息获取成功",
  "data": {
    "application": "学习平台后端",
    "version": "1.0.0",
    "status": "运行中",
    "timestamp": 1634567890123
  },
  "timestamp": 1634567890123
}
```

### 3. Echo测试

**接口地址**: `POST /test/echo`

**请求参数**:
```json
{
  "message": "hello world",
  "test": true
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "数据回显成功",
  "data": {
    "received": {
      "message": "hello world",
      "test": true
    },
    "timestamp": 1634567890123
  },
  "timestamp": 1634567890123
}
```

## 默认测试账号

系统初始化时会创建以下测试账号：

1. **管理员账号**
   - 用户名: `admin`
   - 密码: `123456`
   - 邮箱: `admin@example.com`

2. **测试用户**
   - 用户名: `testuser`
   - 密码: `123456`
   - 邮箱: `test@example.com`

## 错误码说明

- `200`: 操作成功
- `400`: 请求参数错误
- `401`: 未授权（Token无效或过期）
- `403`: 禁止访问
- `404`: 资源未找到
- `500`: 服务器内部错误

## 前端接入示例

### JavaScript (Axios)

```javascript
// 1. 登录
const loginResponse = await axios.post('/api/auth/login', {
  username: 'admin',
  password: '123456'
});

const token = loginResponse.data.data.token;

// 2. 设置Token
axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

// 3. 获取用户信息
const userResponse = await axios.get('/api/auth/me');
console.log(userResponse.data.data);

// 4. 获取用户列表
const usersResponse = await axios.get('/api/users');
console.log(usersResponse.data.data);
```

### Vue.js 示例

```javascript
// main.js
import axios from 'axios'

axios.defaults.baseURL = 'http://localhost:8080/api'

// 请求拦截器
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      // 跳转到登录页面
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

Vue.prototype.$http = axios
```

## 注意事项

1. JWT Token有效期为24小时
2. 所有时间字段使用ISO 8601格式
3. 用户密码在存储时会进行BCrypt加密
4. 当前版本使用内存存储，重启服务后数据会丢失
5. 建议在生产环境中使用数据库存储 