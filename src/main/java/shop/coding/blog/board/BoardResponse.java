package shop.coding.blog.board;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import shop.coding.blog.user.User;

public class BoardResponse {

    @Data
    public static class DetailDTO {
        private Integer id;
        private String title;
        private String content;
        private Integer userId; // 게시글 작성자 id
        private String username;
        private Boolean boardOwner;

        public void isBoardOwner(User sessionUser) {
            if (sessionUser == null) boardOwner = false;
            else boardOwner = sessionUser.getId() == userId;
        }
    }

    @Data
    public static class ReplyDTO {
        private Integer id;
        private Integer userId;
        private String comment;
        private String username;
        private Boolean replyOwner; // 게시글 주인 여부 (세션값과 비교)

        public ReplyDTO(Object[] objects, User sessionUser) {
            this.id = (Integer) objects[0];
            this.userId = (Integer) objects[1];
            this.comment = (String) objects[2];
            this.username = (String) objects[3];

            if (sessionUser == null) replyOwner = false;
            else replyOwner = sessionUser.getId() == userId;
        }
    }
}
