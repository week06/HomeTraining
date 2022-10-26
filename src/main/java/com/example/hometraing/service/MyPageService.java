package com.example.hometraing.service;

import com.example.hometraing.controller.response.BoardResponseDto;
import com.example.hometraing.controller.response.CommentResponseDto;
import com.example.hometraing.domain.*;
import com.example.hometraing.error.ErrorCode;
import com.example.hometraing.security.jwt.TokenProvider;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.example.hometraing.domain.QBoard.board;
import static com.example.hometraing.domain.QMedia.media;

@Slf4j
@RequiredArgsConstructor
@Service
public class MyPageService {

    // QueryDSL 사용
    private final JPAQueryFactory jpaQueryFactory;

    // 토큰 제공자
    private final TokenProvider tokenProvider;


    // Token 및 인증 정보 확인 (한 메소드로 따로 구성하여 만듬)
    public ResponseEntity<?> authorizeToken(HttpServletRequest request){
        // 리프레시 토큰 여부 확인
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseEntity.badRequest().body(ErrorCode.NOT_EXIST_TOKEN.getMessage());
        }

        // 액세스 토큰 여부 확인
        if (null == request.getHeader("Authorization")) {
            return ResponseEntity.badRequest().body(ErrorCode.NOT_EXIST_TOKEN.getMessage());
        }

        // authentication 정보를 저장한 UserDetailsImpl (즉, 인증을 통과한 멤버 정보 저장)
        Member member = tokenProvider.getMemberFromAuthentication();

        // 인증된 멤버가 없다면 에러 메세지 호출
        if (null == member) {
            return ResponseEntity.badRequest().body(ErrorCode.UNUSEFUL_TOKEN.getMessage());
        }

        return ResponseEntity.ok(member);
    }


    // 작성한 게시글 목록 조회
    public ResponseEntity<?> getAllMyBoards(HttpServletRequest request) {

        // 인증 정보 및 Token 여부가 정상적으로 확인이 되면 멤버 정보 저장
        Member member = (Member)authorizeToken(request).getBody();


        // 현재 로그인한 멤버의 작성한 모든 게시글들 출력
        List<Board> myBoardList = jpaQueryFactory
                .selectFrom(board)
                .where(board.member.eq(member))
                .fetch();

        // 가져온 모든 게시글들이 리스트화되어 보여져야 하기 때문에 일부 정보없이 보여지게끔 담는 리스트 생성
        List<BoardResponseDto> myBoardResponseDtoList = new ArrayList<>();

        for (Board board : myBoardList) {
            myBoardResponseDtoList.add(
                    // 카페 게시글 목록 처럼 보여지게끔 형식 저장
                    BoardResponseDto.builder()
                            .id(board.getId())
                            .title(board.getTitle())
                            .author(board.getAuthor())
                            .category(board.getCategory())
                            .createdAt(board.getCreatedAt())
                            .modifiedAt(board.getModifiedAt())
                            .build()
            );
        }

        return ResponseEntity.ok(myBoardResponseDtoList);
    }

    // 작성한 게시글 상세 조회
    @Transactional(readOnly = true)
    public ResponseEntity<?> getMyBoard(Long boardid, HttpServletRequest request) {

        // 인증 정보 및 Token 여부가 정상적으로 확인이 되면 멤버 정보 저장
        Member member = (Member)authorizeToken(request).getBody();

        // 조회하고자 하는 게시글 정보 저장
        Board board1 = jpaQueryFactory
                .selectFrom(board)
                .where(board.id.eq(boardid).and(board.member.eq(member))) // 상세 조회하려는 게시글이 현재 로그인한 멤버의 게시글이 맞는지 확인 후 출력
                .fetchOne();

        // 조회한 게시글에 존재하는 미디어 파일들 전체 불러옴
        List<Media> medias = jpaQueryFactory
                .selectFrom(media)
                .where(media.board.eq(board1))
                .fetch();

        // 조회한 게시글에 존재하는 댓글들 전체 불러옴
        List<Comment> comments = jpaQueryFactory
                .selectFrom(QComment.comment)
                .where(QComment.comment.board.eq(board1))
                .fetch();


        return ResponseEntity.ok(
                BoardResponseDto.builder()
                        .id(board1.getId())
                        .title(board1.getTitle())
                        .author(board1.getAuthor())
                        .content(board1.getContent())
                        .category(board1.getCategory())
                        .medias(medias)
                        .comments(comments)
                        .createdAt(board1.getCreatedAt())
                        .modifiedAt(board1.getModifiedAt())
                        .build()
        );
    }

    // 작성한 댓글들 목록 조회
    public ResponseEntity<?> getAllMyComments(HttpServletRequest request) {

        // 인증 정보 및 Token 여부가 정상적으로 확인이 되면 멤버 정보 저장
        Member member = (Member)authorizeToken(request).getBody();

        // 로그인한 멤버가 작성한 모든 댓글들 저장
        List<Comment> Comments = jpaQueryFactory
                .selectFrom(QComment.comment)
                .where(QComment.comment.member.eq(member))
                .fetch();

        // 게시글 전체 목록 조회와 마찬가지로 카페 게시글 목록처럼 일부 정보는 제외하고 리스트화하여 저장하기 위해 리스트 생성
        List<CommentResponseDto> myCommentslist = new ArrayList<>();

        for (Comment comment : Comments) {
            myCommentslist.add(
                    // 댓글들 저장, (네이버 카페에 있는 나의 작성 댓글 조회를 참고하여 타이틀 대신 내용 그 자리를 대신한다.)
                    CommentResponseDto.builder()
                            .id(comment.getId())
                            .author(comment.getAuthor())
                            .content(comment.getContent())
                            .createdAt(comment.getCreatedAt())
                            .modifiedAt(comment.getModifiedAt())
                            .build()
            );
        }

        return ResponseEntity.ok(myCommentslist);

    }


    // 작성한 댓글 상세 조회
    public ResponseEntity<?> getMyComment(Long commentid, HttpServletRequest request) {

        // 인증 정보 및 Token 여부가 정상적으로 확인이 되면 멤버 정보 저장
        Member member = (Member)authorizeToken(request).getBody();

        // 상세 조회하고자 하는 댓글 저장
        Comment comment = jpaQueryFactory
                .selectFrom(QComment.comment)
                .where(QComment.comment.member.eq(member).and(QComment.comment.id.eq(commentid))) // 현재 로그인한 멤버의 댓글이 맞는지 확인 후 저장
                .fetchOne();

        return ResponseEntity.ok(
                CommentResponseDto.builder()
                        .id(comment.getId())
                        .author(comment.getAuthor())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .modifiedAt(comment.getModifiedAt())
                        .build()
        );

    }
}
