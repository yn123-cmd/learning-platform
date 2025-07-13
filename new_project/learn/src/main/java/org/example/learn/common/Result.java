package org.example.learn.common;

public class Result<T> {
    
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;
    
    public Result() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public Result(Integer code, String message) {
        this();
        this.code = code;
        this.message = message;
    }
    
    public Result(Integer code, String message, T data) {
        this(code, message);
        this.data = data;
    }
    
    // Getter and Setter methods
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    // 成功响应
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功");
    }
    
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }
    
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }
    
    // 失败响应
    public static <T> Result<T> error() {
        return new Result<>(500, "操作失败");
    }
    
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message);
    }
    
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message);
    }
    
    // 参数错误
    public static <T> Result<T> badRequest(String message) {
        return new Result<>(400, message);
    }
    
    // 未授权
    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "未授权");
    }
    
    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(401, message);
    }
    
    // 禁止访问
    public static <T> Result<T> forbidden() {
        return new Result<>(403, "禁止访问");
    }
    
    public static <T> Result<T> forbidden(String message) {
        return new Result<>(403, message);
    }
    
    // 资源未找到
    public static <T> Result<T> notFound() {
        return new Result<>(404, "资源未找到");
    }
    
    public static <T> Result<T> notFound(String message) {
        return new Result<>(404, message);
    }
} 