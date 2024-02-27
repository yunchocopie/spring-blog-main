package shop.coding.blog.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jdk.jfr.Frequency;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import shop.coding.blog.Love.LoveRepository;
import shop.coding.blog.Love.LoveResponse;
import shop.coding.blog.reply.ReplyRepository;
import shop.coding.blog.user.User;
import shop.coding.blog.user.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BoardController {

    private final HttpSession session;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final LoveRepository loveRepository;

    @PostMapping("/board/{id}/update")
    public String update(@PathVariable int id, BoardRequest.UpdateDTO requestDTO) { // @RequestBody 파싱이 JSON 방식으로 바꾼다!
        // 부가 로직 - 인증 체크, 권한 체크
        // 1. 인증 체크 (로그인 확인)
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }
        // 2. 권한 체크 (게시자와 사용자의 권한 확인)
        Board board = boardRepository.findById(id);
        if (board.getUserId() != sessionUser.getId()) {
            return "error/403";
        }
        if (board == null) {
            return "error/400";
        }

        // 3. 핵심 로직 (모델에게 위임)
        // update board_tb set title = ?, content = ? where id = ?
        boardRepository.update(requestDTO, id);

        return "redirect:/board/" + id;
    }

    // 책임 -> 조회해서 화면에 뿌릴려고!
    @GetMapping("/board/{id}/updateForm")
    public String updateForm(@PathVariable int id, HttpServletRequest request) {
        // 인증 체크
        User loginUser = (User) session.getAttribute("sessionUser");
        if (loginUser == null) {
            return "redirect:/loginForm";
        }

        // 권한 체크
        // 모델 위임 (id로 board를 조회) // 조회를 해야 게시글의 주인 ID를 알 수 있다!
        Board board = boardRepository.findById(id);
        if (board.getUserId() != loginUser.getId()) {
            return "error/403";
        }

        // 가방에 담기
        request.setAttribute("board", board);

        return "/board/updateForm";
    }

    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable int id, HttpServletRequest request) { // body 데이터가 없어서 유효성 검사를 할 필요가 없다!
        // 1. 인증 안되면 나가
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) { // 401
            return "redirect:/loginForm";
        }

        // 2. 권한 없으면 나가
        Board board = boardRepository.findById(id);
        if (board.getUserId() != sessionUser.getId()) {
            request.setAttribute("status", 403);
            request.setAttribute("msg", "게시글을 삭제할 권한이 없습니다");
            return "error/40x";
        }

        // 3. 삭제 코드
        boardRepository.deleteById(id);

        return "redirect:/";
    }

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

    @GetMapping("/")
    public String index(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "") String keyword) {
        // isEmpty() -> null, 공백
        // isBlank -> null, 공백, 스페이스
        if (keyword.isBlank()) {
            List<Board> boardList = boardRepository.findAll(page);
            // 전체 페이지 개수
            int count = boardRepository.count().intValue();
            // 5 -> 2page
            // 6 -> 2page
            // 7 -> 3page
            // 8 -> 3page
            int namerge = count % 3 == 0 ? 0 : 1;
            int allPageCount = count / 3 + namerge;

            request.setAttribute("boardList", boardList);
            request.setAttribute("first", page == 0);
            request.setAttribute("last", allPageCount == page + 1);
            request.setAttribute("prev", page - 1);
            request.setAttribute("next", page + 1);
            request.setAttribute("keyword", "");
        } else {
            List<Board> boardList = boardRepository.findAll(page, keyword);
            // 전체 페이지 개수
            int count = boardRepository.count(keyword).intValue();
            // 5 -> 2page
            // 6 -> 2page
            // 7 -> 3page
            // 8 -> 3page
            int namerge = count % 3 == 0 ? 0 : 1;
            int allPageCount = count / 3 + namerge;

            request.setAttribute("boardList", boardList);
            request.setAttribute("first", page == 0);
            request.setAttribute("last", allPageCount == page + 1);
            request.setAttribute("prev", page - 1);
            request.setAttribute("next", page + 1);
            request.setAttribute("keyword", keyword);
        }


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
        User sessionUser = (User) session.getAttribute("sessionUser");
        BoardResponse.DetailDTO boardDTO = boardRepository.findByIdWithUser(id);
        boardDTO.isBoardOwner(sessionUser);

        List<BoardResponse.ReplyDTO> replyDTOList = replyRepository.findByBoardId(id, sessionUser);
        request.setAttribute("board", boardDTO);
        request.setAttribute("replyList", replyDTOList);

        if(sessionUser == null){
            LoveResponse.DetailDTO loveDetailDTO = loveRepository.findLove(id);
            request.setAttribute("love", loveDetailDTO);
        }else{
            LoveResponse.DetailDTO loveDetailDTO = loveRepository.findLove(id, sessionUser.getId());
            request.setAttribute("love", loveDetailDTO);
        }

        // fas fa-heart text-danger
        // far fa-heart
        // request.setAttribute("css", "far fa-heart");

        return "board/detail";
    }
}
