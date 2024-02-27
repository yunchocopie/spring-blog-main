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

    public Love findById(int id) {
        Query query = em.createNativeQuery("select * from love_tb where id = ?", Love.class);
        query.setParameter(1, id);

        Love love = (Love) query.getSingleResult();
        return love;
    }

    @Transactional
    public void deleteById(int id) {
        Query query = em.createNativeQuery("delete from love_tb where id = ?");
        query.setParameter(1, id);

        query.executeUpdate();
    }




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
    public Love save(LoveRequest.SaveDTO requestDTO, int sessionUserId) {
        Query query = em.createNativeQuery("insert into love_tb(board_id, user_id, created_at) values(?,?, now())");
        query.setParameter(1, requestDTO.getBoardId());
        query.setParameter(2, sessionUserId);

        query.executeUpdate();

        Query q = em.createNativeQuery("select * from love_tb where id = (select max(id) from love_tb)", Love.class); // 기본키를 알지 못하기 때문에 안에서 max 값을 알아내야함
        Love love = (Love) q.getSingleResult();
        return love;
    }
}

