package shop.coding.blog.Love;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "love_tb", uniqueConstraints = {
        @UniqueConstraint(
                name="love_uk",
                columnNames={"board_id","user_id"}
        )})
public class Love { // User 1 -> Board N
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer boardId;
    private Integer userId;
    private Timestamp createdAt;
}
