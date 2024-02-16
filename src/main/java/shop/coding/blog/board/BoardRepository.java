package shop.coding.blog.board;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class BoardRepository {
    private final EntityManager em;

    public Long count(String keyword) {
        // 검색된 자료의 수
        Query query = em.createNativeQuery("select count(*) from board_tb where title like ?");
        query.setParameter(1, "%" + keyword + "%");
        return (Long) query.getSingleResult();
    }

    public Long count() {
        Query query = em.createNativeQuery("select count(*) from board_tb");
        return (Long) query.getSingleResult();
    }

    public List<Board> findAll(Integer page, String keyword) {
        // 검색한 자료 조회
        Query query = em.createNativeQuery("select * from board_tb where title like ? order by id desc limit ?, 3", Board.class);
        query.setParameter(1, "%" + keyword + "%");
        query.setParameter(2, page * 3);
        return query.getResultList();
    }

    public List<Board> findAll(Integer page) {
        Query query = em.createNativeQuery("select * from board_tb order by id desc limit ?, 3", Board.class);
        query.setParameter(1, page * 3);
        return query.getResultList();
    }

    @Transactional
    public void save(BoardRequest.SaveDTO requestDTO, int userId) {
        Query query = em.createNativeQuery("insert into board_tb(title, content, user_id, created_at) values(?, ?, ?, now())");
        query.setParameter(1, requestDTO.getTitle());
        query.setParameter(2, requestDTO.getContent());
        query.setParameter(3, userId);

        query.executeUpdate();
    }

    public Board findById(int id) {
        Query query = em.createNativeQuery("select * from board_tb where id = ?", Board.class);
        query.setParameter(1, id);

        Board board = (Board) query.getSingleResult();
        return board;
    }

    @Transactional
    public void deleteById(int id) {
        Query query = em.createNativeQuery("delete from board_tb where id = ?");
        query.setParameter(1, id);
        query.executeUpdate();
    }

    @Transactional
    public void update(BoardRequest.UpdateDTO requestDTO, int id) {
        Query query = em.createNativeQuery("update board_tb set title = ?, content = ? where id = ?");
        query.setParameter(1, requestDTO.getTitle());
        query.setParameter(2, requestDTO.getContent());
        query.setParameter(3, id);
        query.executeUpdate();
    }
}
