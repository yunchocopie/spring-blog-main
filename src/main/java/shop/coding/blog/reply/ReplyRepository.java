package shop.coding.blog.reply;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shop.coding.blog.board.BoardRequest;
import shop.coding.blog.board.BoardResponse;
import shop.coding.blog.user.User;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ReplyRepository {
    private final EntityManager em;

    public List<BoardResponse.ReplyDTO> findByBoardId(int boardId, User sessionUser) {
        String q = """
                select rt.id, rt.user_id, rt.comment, ut.username from reply_tb rt inner join user_tb ut on rt.user_id = ut.id where rt.board_id = ?
                """;
        Query query = em.createNativeQuery(q);
        query.setParameter(1, boardId);

        List<Object[]> rows = query.getResultList();

        // 생성자를 비워두고 넣는 형태
//        for (Object[] row : rows) {
//            BoardResponse.ReplyDTO reply = new BoardResponse.ReplyDTO();
//            reply.setId((int) row[0]);
//            reply.setUserId((int) row[1]);
//            reply.setComment((String) row[2]);
//            reply.setUsername((String) row[3]);
//        }

        return rows.stream().map(row -> new BoardResponse.ReplyDTO(row, sessionUser)).toList();
    }

    @Transactional
    public void save(ReplyRequest.WriteDTO requestDTO, int userId) {
        Query query = em.createNativeQuery("insert into reply_tb(comment, board_id, user_id, created_at) values(?, ?, ?, now())");
        query.setParameter(1, requestDTO.getComment());
        query.setParameter(2, requestDTO.getBoardId());
        query.setParameter(3, userId);

        query.executeUpdate();
    }
}
