package shop.coding.blog.Love;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shop.coding.blog.board.Board;
import shop.coding.blog.board.BoardRequest;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class LoveRepository {
    private final EntityManager em;

    public LoveResponse.DetailDTO findLove(int boardId) {
        String q = """
                SELECT count(*) loveCount
                FROM love_tb
                WHERE board_id = ?;
                """;
        Query query = em.createNativeQuery(q);
        query.setParameter(1, boardId);

        // 한건만 받을때는 바로 받기
        Long loveCount = (Long) query.getSingleResult();
        Integer id = 0;
        Boolean isLove = false;

        System.out.println("id : " + id);
        System.out.println("isLove : " + isLove);
        System.out.println("loveCount : " + loveCount);

        LoveResponse.DetailDTO responseDTO = new LoveResponse.DetailDTO(
                id, isLove, loveCount
        );
        return responseDTO;
    }

    public LoveResponse.DetailDTO findLove(int boardId, int sessionUserId) {
        String q = """
                SELECT
                    id,
                    CASE
                        WHEN user_id IS NULL THEN FALSE
                        ELSE TRUE
                    END AS isLove,
                    (SELECT COUNT(*) FROM love_tb WHERE board_id = ?) AS loveCount
                FROM
                    love_tb
                WHERE
                    board_id = ? AND user_id = ?;
                """;
        Query query = em.createNativeQuery(q);
        query.setParameter(1, boardId);
        query.setParameter(2, boardId);
        query.setParameter(3, sessionUserId);

        Integer id = null;
        Boolean isLove = null;
        Long loveCount = null;
        try {
            Object[] row = (Object[]) query.getSingleResult();
            id = (Integer) row[0];
            isLove = (Boolean) row[1];
            loveCount = (Long) row[2];
        }catch (Exception e){
            id = 0;
            isLove = false;
            loveCount = 0L;
        }


        System.out.println("id : " + id);
        System.out.println("isLove : " + isLove);
        System.out.println("loveCount : " + loveCount);

        LoveResponse.DetailDTO responseDTO = new LoveResponse.DetailDTO(
                id, isLove, loveCount
        );
        return responseDTO;
    }

    @Transactional
    public void save(BoardRequest.SaveDTO requestDTO, int userId) {
        Query query = em.createNativeQuery("insert into board_tb(title, content, user_id, created_at) values(?, ?, ?, now())");
        query.setParameter(1, requestDTO.getTitle());
        query.setParameter(2, requestDTO.getContent());
        query.setParameter(3, userId);

        query.executeUpdate();
    }

    @Transactional
    public void deleteById(int id) {
        Query query = em.createNativeQuery("delete from board_tb where id = ?");
        query.setParameter(1, id);
        query.executeUpdate();
    }

}
