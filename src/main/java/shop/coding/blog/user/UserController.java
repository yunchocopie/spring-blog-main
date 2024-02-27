package shop.coding.blog.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import shop.coding.blog._core.util.ApiUtil;
import shop.coding.blog._core.util.Script;

@RequiredArgsConstructor // final 붙은 애들에 대한 생성자 생성
@Controller
public class UserController {

    // 자바는 final 변수는 반드시 초기화 되어야한다.
    private final UserRepository userRepository;
    private final HttpSession session;

    @GetMapping("/api/username-same-check")
    public @ResponseBody ApiUtil<?> usernameSameCheck(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return new ApiUtil<>(true); // 중복 없으면 true (가입 가능)
        } else {
            return new ApiUtil<>(false); // 중복 존재 (가입 불가능)
        }

    }

    @PostMapping("/login")
    public String login(UserRequest.LoginDTO requestDTO) {
        System.out.println(requestDTO); // toString -> @Data

        if (requestDTO.getUsername().length() < 3) {
            throw new RuntimeException("유저네임 길이가 너무 짧아요");
        }

        User user = userRepository.findByUsername(requestDTO.getUsername());

        if (!BCrypt.checkpw(requestDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("패스워드가 틀렸습니다.");
        }

        session.setAttribute("sessionUser", user); // 락카에 담음 (StateFul)

        return "redirect:/"; // 컨트롤러가 존재하면 무조건 redirect 외우기
    }

    @PostMapping("/join")
    public String join(UserRequest.JoinDTO requestDTO) {
        System.out.println(requestDTO);

        String rawPassword = requestDTO.getPassword();
        String encPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        requestDTO.setPassword(encPassword);

        // ssar을 조회해 보고 있으면!
        try {
            userRepository.save(requestDTO); // Request 한 값을 저장 시킨다.
        } catch (Exception e) {
            throw new RuntimeException("아이디가 중복되었어요");
        }
        return "redirect:/loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "user/loginForm";
    }

    @GetMapping("/user/updateForm")
    public String updateForm() {
        return "user/updateForm";
    }

    @GetMapping("/logout")
    public String logout() {
        session.invalidate(); // 세션을 완전히 삭제. 서랍 비우기.
        return "redirect:/";
    }
}
