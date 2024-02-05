package shop.coding.blog.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository // IoC에 new 하는 방법
public class UserRepository {

    // DB에 접근할 수 있는 매니저 객체
    // 스프링이 만들어서 IoC에 넣어둔다.
    // DI 에서 꺼내 쓰기만 하면 된다.
    private EntityManager em;

    // 생성자 주입 (DI 코드)
    public UserRepository(EntityManager em) {
        this.em = em;
    }

    @Transactional // DB에 Write 할 때는 필수!!
    public void save(UserRequest.JoinDTO requestDTO){
        Query query = em.createNativeQuery("insert into user_tb(username, password, email, created_at) values (?, ?, ?, now())");
        query.setParameter(1, requestDTO.getUsername());
        query.setParameter(2, requestDTO.getPassword());
        query.setParameter(3, requestDTO.getEmail());
        query.executeUpdate();
    }
}
