package com.example.hometraing.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.hometraing.controller.response.BoardResponseDto;

import com.example.hometraing.domain.*;
import com.example.hometraing.error.ErrorCode;
import com.example.hometraing.domain.Board;
import com.example.hometraing.domain.Category;
import com.example.hometraing.domain.Media;
import com.example.hometraing.domain.Member;

import com.example.hometraing.repository.BoardRepository;
import com.example.hometraing.repository.MediaRepository;
import com.example.hometraing.security.jwt.TokenProvider;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.hometraing.domain.QBoard.board;
import static com.example.hometraing.domain.QComment.comment;
import static com.example.hometraing.domain.QMedia.media;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardService {

    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;
    private final TokenProvider tokenProvider;
    private final BoardRepository boardRepository;
    private final MediaRepository mediaRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;


    // 게시글 작성 (S3 이미지 및 동영상 업로드 포함)
    public ResponseEntity<?> writeBoard(List<MultipartFile> multipartFile, HttpServletRequest request) {

        if(!tokenProvider.validateToken(request.getHeader("Refresh-Token"))){
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

        Board board =
                Board.builder()
                        .title(request.getParameter("title"))
                        .author(member.getNickname())
                        .content(request.getParameter("content"))
                        .category(Category.partsValue(Integer.parseInt(request.getParameter("category"))))
                        .member(member)
                        .build();

        boardRepository.save(board);

        List<Media> medias = new ArrayList<>();

        if (multipartFile == null) {
            return ResponseEntity.ok(
                    BoardResponseDto.builder()
                            .id(board.getId())
                            .title(board.getTitle())
                            .author(board.getAuthor())
                            .content(board.getContent())
                            .category(board.getCategory())
                            .createdAt(board.getCreatedAt())
                            .modifiedAt(board.getModifiedAt())
                            .build()
            );
        } else {
            medias = fileUpload(multipartFile, board);
        }


        return ResponseEntity.ok(
                BoardResponseDto.builder()
                        .id(board.getId())
                        .title(board.getTitle())
                        .author(board.getAuthor())
                        .content(board.getContent())
                        .category(board.getCategory())
                        .medias(medias)
                        .createdAt(board.getCreatedAt())
                        .modifiedAt(board.getModifiedAt())
                        .build()
        );
    }


    // 게시글 수정 (미디어 파일 수정)
    @Transactional
    public ResponseEntity<?> updateBoard(List<MultipartFile> multipartFile, Long id, HttpServletRequest request) {
        if(!tokenProvider.validateToken(request.getHeader("Refresh-Token"))){
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
        Board board1 = jpaQueryFactory
                .selectFrom(board)
                .where(board.id.eq(id).and(board.member.eq(member)))
                .fetchOne();

      
        if (board1 == null) {
            return ResponseEntity.badRequest().body(ErrorCode.NOT_EXIST_BOARD.getMessage());
        }

        List<Media> mediaList = jpaQueryFactory
                .selectFrom(media)
                .where(media.board.eq(board1))
                .fetch();

        if (!mediaList.isEmpty()) {
            jpaQueryFactory
                    .delete(media)
                    .where(media.board.eq(board1))
                    .execute();

            for (Media media : mediaList) {
                deleteFile(media.getMediaName());
            }

        }

        List<Media> medias = fileUpload(multipartFile, board1);

        jpaQueryFactory
                .update(board)
                .set(board.content, request.getParameter("content"))
                .where(board.id.eq(id).and(board.member.eq(member)))
                .execute();

        em.clear();

        return ResponseEntity.ok(
                BoardResponseDto.builder()
                        .id(board1.getId())
                        .title(board1.getTitle())
                        .author(board1.getAuthor())
                        .content(board1.getContent())
                        .category(board1.getCategory())
                        .medias(medias)
                        .createdAt(board1.getCreatedAt())
                        .modifiedAt(board1.getModifiedAt())
                        .build()
        );
    }


    // 게시글 삭제
    @Transactional
    public ResponseEntity<?> deleteBoard(Long id, HttpServletRequest request) {
        if(!tokenProvider.validateToken(request.getHeader("Refresh-Token"))){
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

        Board board1 = jpaQueryFactory
                .selectFrom(board)
                .where(board.id.eq(id).and(board.member.eq(member)))
                .fetchOne();

        if (board1 == null) {
            return ResponseEntity.badRequest().body(ErrorCode.NOT_EXIST_BOARD.getMessage());
        }

        // 일반적인 JPA 를 사용했을 때는 cascade를 걸어놓으면 게시글이 삭제 되었을떄 댓글같은 자식 Entity 데이터들도 같이 삭제되었지만
        // queryDSL 은 cascade가 동작되지 않는 것 같다.
        // 따라서 지금은 일단 자식 객체 media 쪽 데이터를 삭제 하고 board 부모 객체를 지우는 것으로 설계한다.
        jpaQueryFactory
                .delete(media)
                .where(media.board.eq(board1))
                .execute();

        jpaQueryFactory
                .delete(board)
                .where(board.id.eq(id))
                .execute();

        return ResponseEntity.ok(true);
    }


    // 게시글 1개 조회
    public ResponseEntity<?> getBoard(Long id, HttpServletRequest request) {

        if(!tokenProvider.validateToken(request.getHeader("Refresh-Token"))){
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

        Board board1 = jpaQueryFactory
                .selectFrom(board)
                .where(board.id.eq(id))
                .fetchOne();

        List<Media> medias = jpaQueryFactory
                .selectFrom(media)
                .where(media.board.eq(board1))
                .fetch();

        List<Comment> comments = jpaQueryFactory
                .selectFrom(comment)
                .where(comment.board.eq(board1))
                .fetch();

        // 댓글 기능과 합쳐 진다면 게시글에 존재하는 댓글들 모두 다 보여져야 함.

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

    // 게시글 전체 목록 조회
    public ResponseEntity<?> getAllBoard(HttpServletRequest request) {
        if(!tokenProvider.validateToken(request.getHeader("Refresh-Token"))){
            return ResponseEntity.badRequest().body(ErrorCode.NOT_EXIST_TOKEN.getMessage());
        }

        List<Board> boards = jpaQueryFactory
                .selectFrom(board)
                .fetch();

        List<BoardResponseDto> boardlist = new ArrayList<>();

        for (Board board : boards) {
            boardlist.add(
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

        return ResponseEntity.ok(boardlist);
    }


    // 미디어 파일들을 받아서 저장
    public List<Media> fileUpload(List<MultipartFile> multipartFile, Board board) {

        // forEach 구문을 통해 multipartFile로 넘어온 파일들 하나씩 fileNameList에 추가
        multipartFile.forEach(file -> {
            String fileName = file.getOriginalFilename();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            System.out.println("for each 진입 : " + fileName);

            try (InputStream inputStream = file.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
            }

            String imagePath = amazonS3.getUrl(bucket, fileName).toString(); // 접근가능한 URL 가져오기

            Media media =
                    Media.builder()
                            .mediaName(fileName)
                            .mediaUrl(imagePath)
                            .board(board)
                            .build();

            mediaRepository.save(media);

        });


        List<Media> medias = jpaQueryFactory
                .selectFrom(media)
                .where(media.board.eq(board))
                .fetch();

        return medias;
    }


    // S3에 저장되어있는 미디어 파일 삭제
    public void deleteFile(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }


    // 먼저 파일 업로드 시, 파일명을 난수화하기 위해 random으로 돌린다. (현재 굳이 할 필요는 없어보임)
    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }


    // file 형식이 잘못된 경우를 확인하기 위해 만들어진 로직이며, 파일 타입과 상관없이 업로드할 수 있게 하기 위해 .의 존재 유무만 판단하였습니다.
    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }

    @Transactional
    public Board isPresentBoard(Long id) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        return optionalBoard.orElse(null);
    }


}
