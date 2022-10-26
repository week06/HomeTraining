package com.example.hometraing.controller;

import com.example.hometraing.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@Controller
// RestController 특성상, FE에서 URL을 매핑하려면 Controller 여야 한다고 생각하지만 RestController 로 변경될 수도 있음. (json 형식의 데이터를 전달할 수도 있기 때문에)
public class BoardController {

    // 게시글 서비스 호출
    private final BoardService boardService;

    // 임의로 생성한 미디어 파일 업로드 테스트용 페이지로 이동
    @GetMapping("/input")
    public String mediaInputTestPage() {
        return "MediaUpdateTest";
    }


    // 게시글 작성 (미디어 포함)
    @ResponseBody // json 형식의 데이터를 전달받을 수 있게끔 ResponseBody 로 설정. RestController를 붙였으면 사용하지 않아도 됨.
    @PostMapping("/board")
    public ResponseEntity<?> writeBoard(
            @RequestPart(value = "data", required = false) List<MultipartFile> multipartFile,
            HttpServletRequest request) throws IOException {
        // @RequestPart를 사용하여 FE 쪽에서 요청받은 미디어 파일들을 multipartFile 타입으로 전달받음
        // FE에서 기입한 게시글 제목, 게시글 내용, 게시글 카테고리를 HttpServletRequest 로 전달받음

        // 파일들이 제대로 전달되었는지 확인
//        for (MultipartFile multipartFile1 : multipartFile) {
//            System.out.println("업로드될 파일 : " + multipartFile1.getOriginalFilename());
//        }

        return boardService.writeBoard(multipartFile, request);

    }


    // 게시글 수정 (미디어 포함)
    @ResponseBody
    @PutMapping("/board/{id}")
    public ResponseEntity<?> updateBoard(@RequestPart(value = "data", required = false) List<MultipartFile> multipartFile,
                                         @PathVariable Long id,
                                         HttpServletRequest request) {

        System.out.println("수정 내용 : " + request.getParameter("content"));
        System.out.println("게시글 id : " + id);
        for (MultipartFile multipartFile1 : multipartFile) {
            System.out.println("수정 업로드될 파일 : " + multipartFile1.getOriginalFilename());
        }

        return boardService.updateBoard(multipartFile, id, request);
    }


    // 게시글 삭제
    @ResponseBody
    @DeleteMapping("/board/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long id, HttpServletRequest request) {
        return boardService.deleteBoard(id, request);
    }


    // 게시글 1개 조회
    @ResponseBody
    @GetMapping("/board/{id}")
    public ResponseEntity<?> getBoard(@PathVariable Long id, HttpServletRequest request) {
        return boardService.getBoard(id, request);
    }


    // 게시글 전체 목록 조회
    @ResponseBody
    @GetMapping("/boards")
    public ResponseEntity<?> getAllBoard(HttpServletRequest request) {

        return boardService.getAllBoard(request);
    }

}
