package shop.coding.blog.board;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "board_tb")
public class Board { // User 1 -> Board N
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String content;

    private int userId; // 테이블에 만들어 질 때 user_id

    private LocalDateTime createdAt;
}
