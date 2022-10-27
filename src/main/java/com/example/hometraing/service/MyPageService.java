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

    private final JPAQueryFactory jpaQueryFactory;

    private final TokenProvider tokenProvider;


    // 작성한 게시글 목록 조회
    public ResponseEntity<?> getAllMyBoards(HttpServletRequest request) {

        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseEntity.badRequest().body(ErrorCode.NOT_EXIST_TOKEN.getMessage());
        }

        // 헤더에 엑세스 토큰 없으면 "MEMBER_NOT_FOUND" , "로그인이 필요합니다." 노출
        if (null == request.getHeader("Authorization")) {
            return ResponseEntity.badRequest().body(ErrorCode.NOT_EXIST_TOKEN.getMessage());
        }

        // Member 인증통과 안되면, INVALID_TOKEN , "Token이 유효하지 않습니다." 노출
        Member member = tokenProvider.getMemberFromAuthentication();

        if (null == member) {
            return ResponseEntity.badRequest().body(ErrorCode.UNUSEFUL_TOKEN.getMessage());
        }

        List<Board> myBoardList = jpaQueryFactory
                .selectFrom(board)
                .where(board.member.eq(member))
                .fetch();

        List<BoardResponseDto> myBoardResponseDtoList = new ArrayList<>();

        for (Board board : myBoardList) {
            myBoardResponseDtoList.add(
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

        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseEntity.badRequest().body(ErrorCode.NOT_EXIST_TOKEN.getMessage());
        }

        // 헤더에 엑세스 토큰 없으면 "MEMBER_NOT_FOUND" , "로그인이 필요합니다." 노출
        if (null == request.getHeader("Authorization")) {
            return ResponseEntity.badRequest().body(ErrorCode.NOT_EXIST_TOKEN.getMessage());
        }

        // Member 인증통과 안되면, INVALID_TOKEN , "Token이 유효하지 않습니다." 노출
        Member member = tokenProvider.getMemberFromAuthentication();

        if (null == member) {
            return ResponseEntity.badRequest().body(ErrorCode.UNUSEFUL_TOKEN.getMessage());
        }

        //Post 있어야 comment 읽기 가능 => Post NULL일 경우 Error
        Board board1 = jpaQueryFactory
                .selectFrom(board)
                .where(board.id.eq(boardid).and(board.member.eq(member)))
                .fetchOne();

        if (board1 == null) {
            return ResponseEntity.badRequest().body(ErrorCode.NOT_EXIST_BOARD.getMessage());
        }

        List<Media> medias = jpaQueryFactory
                .selectFrom(media)
                .where(media.board.eq(board1))
                .fetch();

        //post 있으면 comment 다 가져오자
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

        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseEntity.badRequest().body(ErrorCode.NOT_EXIST_TOKEN.getMessage());
        }

        // 헤더에 엑세스 토큰 없으면 "MEMBER_NOT_FOUND" , "로그인이 필요합니다." 노출
        if (null == request.getHeader("Authorization")) {
            return ResponseEntity.badRequest().body(ErrorCode.NOT_EXIST_TOKEN.getMessage());
        }

        // Member 인증통과 안되면, INVALID_TOKEN , "Token이 유효하지 않습니다." 노출
        Member member = tokenProvider.getMemberFromAuthentication();

        if (null == member) {
            return ResponseEntity.badRequest().body(ErrorCode.UNUSEFUL_TOKEN.getMessage());
        }

        List<Comment> Comments = jpaQueryFactory
                .selectFrom(QComment.comment)
                .where(QComment.comment.member.eq(member))
                .fetch();

        List<CommentResponseDto> myCommentslist = new ArrayList<>();

        for (Comment comment : Comments) {
            myCommentslist.add(
                    CommentResponseDto.builder()
                            .id(comment.getId())
                            .author(comment.getMember().getNickname())
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

        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseEntity.badRequest().body(ErrorCode.NOT_EXIST_TOKEN.getMessage());
        }

        // 헤더에 엑세스 토큰 없으면 "MEMBER_NOT_FOUND" , "로그인이 필요합니다." 노출
        if (null == request.getHeader("Authorization")) {
            return ResponseEntity.badRequest().body(ErrorCode.NOT_EXIST_TOKEN.getMessage());
        }

        // Member 인증통과 안되면, INVALID_TOKEN , "Token이 유효하지 않습니다." 노출
        Member member = tokenProvider.getMemberFromAuthentication();

        if (null == member) {
            return ResponseEntity.badRequest().body(ErrorCode.UNUSEFUL_TOKEN.getMessage());
        }

        Comment comment = jpaQueryFactory
                .selectFrom(QComment.comment)
                .where(QComment.comment.member.eq(member).and(QComment.comment.id.eq(commentid)))
                .fetchOne();

        return ResponseEntity.ok(
                CommentResponseDto.builder()
                        .id(comment.getId())
                        .author(comment.getMember().getNickname())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .modifiedAt(comment.getModifiedAt())
                        .build()
        );

    }
}
