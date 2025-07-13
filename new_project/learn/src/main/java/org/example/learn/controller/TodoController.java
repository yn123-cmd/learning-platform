package org.example.learn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.learn.common.Result;
import org.example.learn.entity.Todo;
import org.example.learn.service.TodoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 待办事项控制器
 */
@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "待办事项管理", description = "待办事项相关接口")
public class TodoController {
    
    private final TodoService todoService;
    
    @Operation(summary = "获取待办事项列表", description = "获取当前用户的所有待办事项")
    @GetMapping
    public Result<List<Todo>> getTodos() {
        try {
            List<Todo> todos = todoService.getAllTodos();
            return Result.success(todos);
        } catch (Exception e) {
            log.error("获取待办事项列表失败", e);
            return Result.error("获取待办事项列表失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "创建待办事项", description = "创建新的待办事项")
    @PostMapping
    public Result<Todo> createTodo(@RequestBody Todo todo) {
        try {
            Todo createdTodo = todoService.createTodo(todo);
            return Result.success("待办事项创建成功", createdTodo);
        } catch (Exception e) {
            log.error("创建待办事项失败", e);
            return Result.error("创建待办事项失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "更新待办事项", description = "更新指定的待办事项")
    @PutMapping("/{todoId}")
    public Result<Todo> updateTodo(
            @Parameter(description = "待办事项ID") @PathVariable Long todoId,
            @RequestBody Todo todo) {
        try {
            Todo updatedTodo = todoService.updateTodo(todoId, todo);
            return Result.success("待办事项更新成功", updatedTodo);
        } catch (Exception e) {
            log.error("更新待办事项失败: todoId={}", todoId, e);
            return Result.error("更新待办事项失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "删除待办事项", description = "删除指定的待办事项")
    @DeleteMapping("/{todoId}")
    public Result<Boolean> deleteTodo(
            @Parameter(description = "待办事项ID") @PathVariable Long todoId) {
        try {
            boolean deleted = todoService.deleteTodo(todoId);
            if (deleted) {
                return Result.success("待办事项删除成功", true);
            } else {
                return Result.error("待办事项不存在或删除失败");
            }
        } catch (Exception e) {
            log.error("删除待办事项失败: todoId={}", todoId, e);
            return Result.error("删除待办事项失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "根据状态查询待办事项", description = "根据状态查询待办事项列表")
    @GetMapping("/status/{status}")
    public Result<List<Todo>> getTodosByStatus(
            @Parameter(description = "待办事项状态") @PathVariable String status) {
        try {
            Todo.TodoStatus todoStatus = Todo.TodoStatus.valueOf(status.toUpperCase());
            List<Todo> todos = todoService.getTodosByStatus(todoStatus);
            return Result.success(todos);
        } catch (IllegalArgumentException e) {
            log.error("无效的待办事项状态: {}", status, e);
            return Result.error("无效的待办事项状态: " + status);
        } catch (Exception e) {
            log.error("根据状态查询待办事项失败: status={}", status, e);
            return Result.error("查询待办事项失败: " + e.getMessage());
        }
    }
} 