package com.example.hometraing.controller;

import com.example.hometraing.controller.request.BoardRequestDto;
import com.example.hometraing.domain.Category;
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


    // 게시글 작성 (미디어 포함)
    @ResponseBody // json 형식의 데이터를 전달받을 수 있게끔 ResponseBody 로 설정. RestController를 붙였으면 사용하지 않아도 됨.
    @PostMapping(value = "/board")
    public ResponseEntity<?> writeBoard(
            @RequestPart(value = "file", required = false) List<MultipartFile> multipartFile, // @RequestPart를 사용하여 FE 쪽에서 요청받은 미디어 파일들을 multipartFile 타입으로 전달받음
            HttpServletRequest request,
            @RequestBody BoardRequestDto boardRequestDto) throws IOException { // FE에서 기입한 게시글 제목, 게시글 내용, 게시글 카테고리를 BoardRequestDto로 전달받음

        System.out.println("title : " + boardRequestDto.getTitle());
        System.out.println("content : " + boardRequestDto.getContent());
        System.out.println("category : " + Category.partsValue(Integer.parseInt(boardRequestDto.getCategory())));
        System.out.println("image : " + multipartFile);

        return boardService.writeBoard(multipartFile, request, boardRequestDto);

    }


    // 게시글 수정 (미디어 포함)
    @ResponseBody
    @PutMapping("/board/{boardid}")
    public ResponseEntity<?> updateBoard(
            @RequestPart(value = "data", required = false) List<MultipartFile> multipartFile, // 수정할 미디어 파일들
            @PathVariable Long boardid, // 수정하고자 하는 게시글 id
            HttpServletRequest request, // 요청 인증 정보 확인을 위한 HttpServletRequest
            @RequestBody BoardRequestDto boardRequestDto) { // 게시글 수정내용

        return boardService.updateBoard(multipartFile, boardid, request, boardRequestDto);
    }


    // 게시글 삭제
    @ResponseBody
    @DeleteMapping("/board/{boardid}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long boardid, // 삭제하고자 하는 게시글 id
                                         HttpServletRequest request) { // 요청 인증 정보 확인을 위한 HttpServletRequest
        return boardService.deleteBoard(boardid, request);
    }


    // 게시글 1개 조회
    @ResponseBody
    @GetMapping("/board/{id}")
    public ResponseEntity<?> getBoard(@PathVariable Long id, // 조회하고자 하는 게시글 id
                                      HttpServletRequest request) { // 요청 인증 정보 확인을 위한 HttpServletRequest
        return boardService.getBoard(id, request);
    }


    // 게시글 전체 목록 조회
    @ResponseBody
    @GetMapping("/boards")
    public ResponseEntity<?> getAllBoard(HttpServletRequest request) { // 요청 인증 정보 확인을 위한 HttpServletRequest.
        // 현재 구현한 사이트 자체가 로그인한 사람들만이 이용할 수 있기 때문에 인증 정보가 필요하다.

        return boardService.getAllBoard(request);
    }

}
