package com.example.hometraing.controller.response;

import com.example.hometraing.domain.Category;
import com.example.hometraing.domain.Comment;
import com.example.hometraing.domain.Media;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@Builder
@Getter
public class BoardResponseDto { // 최종적으로 FE로 전달될 json 데이터들
    // 게시글 id
    private Long id;

    // 작성한 게시글 제목
    private String title;

    // 게시글 작성자 (현재 로그인한 유저 정보)
    private String author;

    // 작성한 게시글 내용
    private String content;

    // 선택한 카테고리 값 (현재 BE 쪽에선 숫자 값으로 넘겨받은 뒤 그에 해당하는 카테고리 값을 전달하도록 설정함)
    private Category category;

    // 게시글에 포함된 미디어 데이터들 (없어도 됨.)
    private List<Media> medias;

    // 댓글 기능 추합 시, 해당 게시글에 존재하는 댓글들 출력 필요 가능성 잇음.
    private List<Comment> comments;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;
}
