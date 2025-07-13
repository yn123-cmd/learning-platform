package org.example.learn.repository;

import org.example.learn.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 根据用户名和状态查找用户
     */
    @Query("SELECT u FROM User u WHERE u.username = ?1 AND u.status = ?2")
    Optional<User> findByUsernameAndStatus(String username, Integer status);
    
    /**
     * 根据角色统计用户数量
     */
    Long countByRole(String role);
    
    /**
     * 根据角色查询用户列表
     */
    List<User> findByRole(String role);
    
    /**
     * 根据状态查询用户
     */
    List<User> findByStatus(Integer status);
    
    /**
     * 统计活跃用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 1")
    Long countActiveUsers();
    
    /**
     * 查询所有教师
     */
    @Query("SELECT u FROM User u WHERE u.role = 'TEACHER' AND u.status = 1")
    List<User> findAllTeachers();
    
    /**
     * 查询所有学生
     */
    @Query("SELECT u FROM User u WHERE u.role = 'USER' AND u.status = 1")
    List<User> findAllStudents();
} 