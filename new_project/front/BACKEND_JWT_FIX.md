# 后端JWT配置修复指南

## 问题描述

后端返回JWT签名密钥错误：
```
The signing key's size is 400 bits which is not secure enough for the HS512 algorithm.
```

HS512算法要求密钥长度至少为512位（64字节），当前密钥只有400位，不够安全。

## 解决方案

### 方案1：生成新的安全密钥

**Java后端示例**：
```java
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

// 生成安全的HS512密钥
SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
String base64Key = Encoders.BASE64.encode(key.getEncoded());
System.out.println("新的安全密钥: " + base64Key);
```

### 方案2：手动生成512位密钥

**在线生成**：
- 访问 https://www.allkeysgenerator.com/Random/Security-Encryption-Key-Generator.aspx
- 选择 512 bit 长度
- 复制生成的密钥

**命令行生成**：
```bash
# 生成64字节（512位）的随机密钥
openssl rand -base64 64
```

### 方案3：更换为更短的算法

如果不想更换密钥，可以改用HS256算法（需要256位密钥）：

```java
// 使用HS256替代HS512
.signWith(SignatureAlgorithm.HS256, secretKey)
```

## 配置示例

### Spring Boot配置

**application.yml**:
```yaml
jwt:
  secret: "你的64字节Base64编码密钥"
  expiration: 86400 # 24小时
```

**JWT工具类**:
```java
@Component
public class JwtUtils {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public String generateToken(String username) {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }
}
```

## 测试步骤

1. 更新后端JWT配置
2. 重启后端服务
3. 清除浏览器localStorage中的旧token
4. 重新登录测试

## 安全建议

1. **使用环境变量**：不要将密钥硬编码在代码中
2. **定期轮换**：定期更换JWT签名密钥
3. **强随机性**：使用加密安全的随机数生成器
4. **合适长度**：HS512使用至少512位，HS256使用至少256位

## 临时解决方案

如果急需测试，可以临时使用以下64字节密钥：
```
your-512-bit-secret-key-here-must-be-at-least-64-characters-long-for-hs512-algorithm-to-work-properly
```

**注意**：这只是用于开发测试，生产环境必须使用安全生成的密钥！ 