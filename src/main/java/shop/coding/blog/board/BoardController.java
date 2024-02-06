package shop.coding.blog.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jdk.jfr.Frequency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import shop.coding.blog.user.User;
import shop.coding.blog.user.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BoardController {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final HttpSession session;

    @PostMapping("/board/save")
    public String save(BoardRequest.SaveDTO requestDTO, HttpServletRequest request) {
        // 1. 인증 체크
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        // 2. 바디 데이터 확인 및 유효성 검사
        System.out.println(requestDTO);

        if (requestDTO.getTitle().length() > 30) {
            request.setAttribute("status", 400);
            request.setAttribute("msg", "title의 길이가 30자를 초과해서는 안되요");
            return "error/40x"; // BadRequest
        }

        // 3. 모델 위임
        // insert into board_tb(title, content, user_id, created_at) values(?, ?, ?, now());
        boardRepository.save(requestDTO, sessionUser.getId()); // 모델에게 위임한다. '?' 3건을!

        return "redirect:/";
    }

    @GetMapping({"/", "/board"})
    public String index(HttpServletRequest request) {
        List<Board> boardList = boardRepository.findAll();
        request.setAttribute("boardList", boardList);

        return "index";
    }

    @GetMapping("/board/saveForm")
    public String saveForm() {
        // 이 코드는 공통 로직으로 빼는 것이 더 좋다!
        // 필터에서 든지 Dispercher 에서 라든지
        // session 영역에 sessionUser 키값에 user 객체 있는지 체크
        User sessionUser = (User) session.getAttribute("sessionUser");

        // 값이 null 이면 로그인 페이지로 리다이렉션
        // 값이 null 이 아니면, /board/saveForm 으로 이동
        if (sessionUser == null) {
            return "redirect:/loginFrom";
        }
        return "board/saveForm";
    }

    @GetMapping("/board/{id}")
    public String detail(@PathVariable int id, HttpServletRequest request) {
        BoardResponse.DetailDTO responseDTO = userRepository.findById(id);

        request.setAttribute("board", responseDTO);
        return "board/detail";
    }
}
