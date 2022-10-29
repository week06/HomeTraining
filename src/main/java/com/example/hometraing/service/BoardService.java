package com.example.hometraing.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.hometraing.controller.request.BoardRequestDto;
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
import java.util.UUID;

import static com.example.hometraing.domain.QBoard.board;
import static com.example.hometraing.domain.QComment.comment;
import static com.example.hometraing.domain.QMedia.media;
import static com.example.hometraing.domain.QMember.member;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardService {

    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;
    private final TokenProvider tokenProvider;
    private final BoardRepository boardRepository;
    private final MediaRepository mediaRepository;

    // S3 버킷
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // S3 서비스
    private final AmazonS3 amazonS3;

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


    // 게시글 작성 (S3 이미지 및 동영상 업로드 포함)
    public ResponseEntity<?> writeBoard(List<MultipartFile> multipartFile, HttpServletRequest request, BoardRequestDto boardRequestDto) {

        // 인증 정보 및 Token 여부가 정상적으로 확인이 되면 멤버 정보 저장
//        Member member = (Member)authorizeToken(request).getBody();

        // 임의 멤버 생성
        Member member1 = jpaQueryFactory.selectFrom(member).where(member.id.eq(1L)).fetchOne();

        // member1 을 member로 바꿔야한다.
        // 게시글 엔티티에 빌더를 사용하여 작성한 정보값들 대입
        Board board =
                Board.builder()
                        .title(boardRequestDto.getTitle()) // 입력 요청한 제목값
                        .author(member1.getNickname()) // 작성하고자 하는 멤버의 닉네임을 가져옴
                        .content(boardRequestDto.getContent()) // 입력 요청한 게시글 내용
                        .category(Category.partsValue(Integer.parseInt(boardRequestDto.getCategory()))) // 카테고리 값을 입력받으면 해당 값에 맞는 카테고리 명 대입
                        .member(member1) // 작성한 멤버의 객체 정보 대입, 멤버의 고유 Id로써 author 속성과 함께 구분할 수 있게끔 도와줌
                        .build();

        // 작성한 게시글 정보 저장
        boardRepository.save(board);

        // 게시글에 미디어 파일들도 같이 업로드할 시, 저장할 리트스
        List<Media> medias = new ArrayList<>();

        // 게시글에 미디어 파일들을 업로드하지 않을 수도 있으므로, 없다면 미디어 리스트는 null 인상태로 출력
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
        } else {// 업로드할 미디어 파일들이 있다면, fileUpload 메소드 실행 (미디어 파일들과  함께 해당 게시글의 정보를 인자값으로 들고감)
            medias = fileUpload(multipartFile, board);
        }

        // 작성한 게시글 정보, 미디어 파일들 출력
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
    public ResponseEntity<?> updateBoard(List<MultipartFile> multipartFile, Long boardid, HttpServletRequest request, BoardRequestDto boardRequestDto) {

        // 인증 정보 및 Token 여부가 정상적으로 확인이 되면 멤버 정보 저장
        Member member = (Member)authorizeToken(request).getBody();


        // 수정할 게시글 정보 불러옴
        Board board1 = jpaQueryFactory
                .selectFrom(board)
                .where(board.id.eq(boardid).and(board.member.eq(member))) // 수정하고자 선택한 게시글이 현재 로그인한 멤버가 작성한 게시글이 맞는지 여부 확인
                .fetchOne();

        // 수정하고자 선택한 게시글이 현재 로그인한 멤버가 작성한 게시글이 아니라면 null 이 뜨고 에러 메세지 출력
        if (board1 == null) {
            return ResponseEntity.badRequest().body(ErrorCode.NOT_EXIST_BOARD.getMessage());
        }

        // 선택한 게시글에 존재하는 미디어 파일들 전부 불러옴.
        List<Media> mediaList = jpaQueryFactory
                .selectFrom(media)
                .where(media.board.eq(board1))
                .fetch();

        // 미디어 파일들을  수정하려 한다면 기존에 DB에 저장되어있는 미디어 파일들과 S3에 저장되어있는 미디어 파일들 삭제 후
        // 다시 저장하는 형식으로 구현
        // 이유 : 삭제없이 그냥 미디어 파일들을 수정하여 저장하려 한다면 삭제되지 않은 기존 미디어 파일들이 DB 혹은 S3에 계속 저장되어있을 것이므로
        //       삭제 후 다시 업로드 하는 형식으로 설계함.

        // 미디어 파일들이 존재하고, 삭제하려 한다면
        if (!mediaList.isEmpty()) {
            jpaQueryFactory // (1) DB에서 제거
                    .delete(media)
                    .where(media.board.eq(board1))
                    .execute();

            for (Media media : mediaList) { // (2) S3에서 제거
                deleteFile(media.getMediaName());
            }

            // 만약, 미디어 파일들이 존재하고, 삭제하려 하지 않아도 삭제되었다가 다시 저장이 될 것이다.
        }

        // 새로 수정되어 들어올 미디어 파일들을 저장할 리스트 생성
        List<Media> medias = new ArrayList<>();

        // 새로 수정할 미디어 파일들이 존재한다면 파일 업로드 실행 (update)
        if (multipartFile != null) {
            medias = fileUpload(multipartFile, board1);
        }


        // 게시글에 대한 내용은 따로 수정
        jpaQueryFactory
                .update(board)
                .set(board.content, boardRequestDto.getContent())
                .where(board.id.eq(boardid).and(board.member.eq(member))) // 해당 게시글의 작성자가 현재 수정하고자 하는 멤버와 같은 사람인지 파악 후 실행
                .execute();

        // QueryDSl의 경우 update 문을 사용하려면 반드시 clear() 를 사용해야 영속성 컨텍스트까지 반영이 된다. (자매품 flush() 도 있음)
        em.clear();

        return ResponseEntity.ok(
                // 수정이 되었다면 수정된 게시글 내용 전부 출력
                BoardResponseDto.builder()
                        .id(board1.getId())
                        .title(board1.getTitle())
                        .author(board1.getAuthor())
                        .content(board1.getContent())
                        .category(board1.getCategory())
                        .medias(medias)
                        // 댓글기능 합치면 댓글 리스트도 다 나올 수 있게끔 구현 예정
                        .createdAt(board1.getCreatedAt())
                        .modifiedAt(board1.getModifiedAt())
                        .build()
        );
    }


    // 게시글 삭제
    @Transactional
    public ResponseEntity<?> deleteBoard(Long boardid, HttpServletRequest request) {

        // 인증 정보 및 Token 여부가 정상적으로 확인이 되면 멤버 정보 저장
        Member member = (Member)authorizeToken(request).getBody();

        // 삭제할 게시글 정보 저장
        Board board1 = jpaQueryFactory
                .selectFrom(board)
                .where(board.id.eq(boardid).and(board.member.eq(member))) // 삭제하고자 선택한 게시글이 현재 로그인한 멤버가 작성한 게시글이 맞는지 여부 확인
                .fetchOne();

        // 삭제하고자 선택한 게시글이 현재 로그인한 멤버가 작성한 게시글이 아니라면 null 이 뜨고 에러 메세지 출력
        if (board1 == null) {
            return ResponseEntity.badRequest().body(ErrorCode.NOT_EXIST_BOARD.getMessage());
        }

        // 일반적인 JPA 를 사용했을 때는 cascade를 걸어놓으면 게시글이 삭제 되었을떄 댓글같은 자식 Entity 데이터들도 같이 삭제되었지만
        // queryDSL 은 cascade가 동작되지 않는 것 같다.
        // 따라서 지금은 일단 자식 객체 media, comment 쪽 데이터를 먼저 삭제 하고 board 부모 객체를 지우는 것으로 설계한다.

        // 미디어 파일 먼저 삭제
        jpaQueryFactory
                .delete(media)
                .where(media.board.eq(board1))
                .execute();

        // 댓글 기능이 합쳐지면 해제하도록 한다.
        // 댓글 삭제
//        jpaQueryFactory
//                .delete(comment)
//                .where(comment.board.eq(board1))
//                .execute();

        // 마지막 게시글 삭제
        jpaQueryFactory
                .delete(board)
                .where(board.id.eq(boardid))
                .execute();

        // 삭제가 정상적으로 되었다면 true 값 반환
        return ResponseEntity.ok(true);
    }


    // 게시글 1개 조회
    public ResponseEntity<?> getBoard(Long id, HttpServletRequest request) {

        // 인증 정보 및 Token 여부가 정상적으로 확인이 되면 멤버 정보 저장
        Member member = (Member)authorizeToken(request).getBody();

        // 조회하고자 선택한 게시글 정보 불러옴
        Board board1 = jpaQueryFactory
                .selectFrom(board)
                .where(board.id.eq(id))
                .fetchOne();

        // 게시글을 불러오면서 같이 존재하는 미디어 파일들도 불러옴 (없다면 null로 불러와짐)
        List<Media> medias = jpaQueryFactory
                .selectFrom(media)
                .where(media.board.eq(board1))
                .fetch();

        // 게시글을 불러오면서 같이 존재하는 댓글들도 불러옴
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
        // 리프레시 토큰 여부 확인
//        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
//            return ResponseEntity.badRequest().body(ErrorCode.NOT_EXIST_TOKEN.getMessage());
//        }
//
//        // 액세스 토큰 여부 확인
//        if (null == request.getHeader("Authorization")) {
//            return ResponseEntity.badRequest().body(ErrorCode.NOT_EXIST_TOKEN.getMessage());
//        }

        // 작성된 모든 게시글들 모두 불러옴
        List<Board> boards = jpaQueryFactory
                .selectFrom(board)
                .fetch();

        // 가져온 모든 게시글들이 리스트화되어 보여져야 하기 때문에 일부 정보없이 보여지게끔 담는 리스트 생성
        List<BoardResponseDto> boardlist = new ArrayList<>();

        for (Board board : boards) {
            boardlist.add(
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


        return ResponseEntity.ok(boardlist);
    }


    // 미디어 파일들을 받아서 저장
    public List<Media> fileUpload(List<MultipartFile> multipartFile, Board board) {

        // forEach 구문을 통해 multipartFile로 넘어온 미디어 파일들 하나씩 조회
        multipartFile.forEach(file -> {
            String fileName = file.getOriginalFilename(); // 각 파일의 이름을 저장
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            System.out.println("for each 진입 : " + fileName);

            try (InputStream inputStream = file.getInputStream()) {
                // S3에 업로드 및 저장
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
            }

            // 접근가능한 URL 가져오기
            String imagePath = amazonS3.getUrl(bucket, fileName).toString();

            // 동시에 해당 미디어 파일들을 미디어 DB에 이름과 Url 정보 저장.
            // 게시글마다 어떤 미디어 파일들을 포함하고 있는지 파악하기 위함 또는 활용하기 위함.
            Media media =
                    Media.builder()
                            .mediaName(fileName)
                            .mediaUrl(imagePath)
                            .board(board)
                            .build();

            mediaRepository.save(media);

        });

        // 저장한 해당 게시글에 속한 미디어 파일들 다시 반환
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


    // 먼저 파일 업로드 시, 파일명을 난수화하기 위해 random으로 돌린다. (현재는 굳이 난수화할 필요가 없어보여 사용하지 않음)
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

//    @Transactional
//    public Board isPresentBoard(Long id) {
//        Optional<Board> optionalBoard = boardRepository.findById(id);
//        return optionalBoard.orElse(null);
//    }


}
