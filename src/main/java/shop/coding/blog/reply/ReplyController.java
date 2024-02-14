package shop.coding.blog.reply;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import shop.coding.blog.user.User;

// 댓글쓰기, 댓글삭제, 댓글 목록보기
@RequiredArgsConstructor
@Controller
public class ReplyController {

    private final HttpSession session;
    private final ReplyRepository replyRepository;

    @PostMapping("/reply/save")
    public String write(ReplyRequest.WriteDTO requestDTO) {
        System.out.println(requestDTO);

        User sessionUser = (User) session.getAttribute("sessionUser");

        if (sessionUser == null) {
            return "redirect:/loginFrom";
        }

        // 유효성 검사

        // 핵심 코드
        replyRepository.save(requestDTO, sessionUser.getId());

        return "redirect:/board/" + requestDTO.getBoardId();
    }
}
