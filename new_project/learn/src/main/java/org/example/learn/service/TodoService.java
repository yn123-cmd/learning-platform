package org.example.learn.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.learn.entity.Todo;
import org.example.learn.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 待办事项服务类
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TodoService {
    
    private final TodoRepository todoRepository;
    
    /**
     * 获取所有待办事项
     */
    @Transactional(readOnly = true)
    public List<Todo> getAllTodos() {
        return todoRepository.findByOrderByCreateTimeDesc();
    }
    
    /**
     * 根据状态查询待办事项
     */
    @Transactional(readOnly = true)
    public List<Todo> getTodosByStatus(Todo.TodoStatus status) {
        return todoRepository.findByStatus(status);
    }
    
    /**
     * 根据优先级查询待办事项
     */
    @Transactional(readOnly = true)
    public List<Todo> getTodosByPriority(Todo.Priority priority) {
        return todoRepository.findByPriority(priority);
    }
    
    /**
     * 创建待办事项
     */
    public Todo createTodo(Todo todo) {
        // 设置默认值
        if (todo.getStatus() == null) {
            todo.setStatus(Todo.TodoStatus.PENDING);
        }
        if (todo.getPriority() == null) {
            todo.setPriority(Todo.Priority.NORMAL);
        }
        
        return todoRepository.save(todo);
    }
    
    /**
     * 更新待办事项
     */
    public Todo updateTodo(Long todoId, Todo todoData) {
        Optional<Todo> existingTodo = todoRepository.findById(todoId);
        if (existingTodo.isEmpty()) {
            throw new RuntimeException("待办事项不存在: " + todoId);
        }
        
        Todo todo = existingTodo.get();
        
        // 更新字段
        if (todoData.getTitle() != null) {
            todo.setTitle(todoData.getTitle());
        }
        if (todoData.getDescription() != null) {
            todo.setDescription(todoData.getDescription());
        }
        if (todoData.getStatus() != null) {
            todo.setStatus(todoData.getStatus());
        }
        if (todoData.getPriority() != null) {
            todo.setPriority(todoData.getPriority());
        }
        if (todoData.getCategory() != null) {
            todo.setCategory(todoData.getCategory());
        }
        if (todoData.getDueDate() != null) {
            todo.setDueDate(todoData.getDueDate());
        }
        if (todoData.getCompletedAt() != null) {
            todo.setCompletedAt(todoData.getCompletedAt());
        }
        
        // 如果状态改为完成，设置完成时间
        if (todoData.getStatus() == Todo.TodoStatus.COMPLETED && todo.getCompletedAt() == null) {
            todo.setCompletedAt(LocalDateTime.now());
        }
        
        return todoRepository.save(todo);
    }
    
    /**
     * 删除待办事项
     */
    public boolean deleteTodo(Long todoId) {
        if (todoRepository.existsById(todoId)) {
            todoRepository.deleteById(todoId);
            return true;
        }
        return false;
    }
    
    /**
     * 完成待办事项
     */
    public Todo completeTodo(Long todoId) {
        Optional<Todo> existingTodo = todoRepository.findById(todoId);
        if (existingTodo.isEmpty()) {
            throw new RuntimeException("待办事项不存在: " + todoId);
        }
        
        Todo todo = existingTodo.get();
        todo.setStatus(Todo.TodoStatus.COMPLETED);
        todo.setCompletedAt(LocalDateTime.now());
        
        return todoRepository.save(todo);
    }
    
    /**
     * 查询即将到期的待办事项
     */
    @Transactional(readOnly = true)
    public List<Todo> getDueSoonTodos() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);
        
        return todoRepository.findDueSoon(now, tomorrow);
    }
    
    /**
     * 查询已过期的待办事项
     */
    @Transactional(readOnly = true)
    public List<Todo> getOverdueTodos() {
        return todoRepository.findOverdue(LocalDateTime.now());
    }
} 