package shop.coding.blog.user;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_tb")
public class User {
    @Id // PK 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment 어노테이션
    private int id;
    private String username;
    private String password;
    private String email;
    private LocalDateTime createdAt;
}
