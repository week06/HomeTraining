//package com.example.hometraing.service;
//
//import com.example.hometraing.controller.response.MypageResponseDto;
//import com.example.hometraing.controller.response.ResponseDto;
//import com.example.hometraing.domain.Board;
//import com.example.hometraing.security.jwt.TokenProvider;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.ArrayList;
//import java.util.List;
//
//@RequiredArgsConstructor
//@Service
//public class MyPageService {
//    private final PostRepository postRepository;
//    private final LikePostRepository likePostRepository;
//
//    private final CommentRepository commentRepository;
//    private final LikeCommentRepository likeCommentRepository;
//
//    // CommentRepository 추가해야함
//    private final TokenProvider tokenProvider;
//
//    public ResponseDto<?> getMyPage(HttpServletRequest request) {
//        if (null == request.getHeader("Refresh-Token")) {
//            return ResponseDto.fail("MEMBER_NOT_FOUND",
//                    "로그인이 필요합니다.");
//        }
//
//        // 헤더에 엑세스 토큰 없으면 "MEMBER_NOT_FOUND" , "로그인이 필요합니다." 노출
//        if (null == request.getHeader("Authorization")) {
//            return ResponseDto.fail("MEMBER_NOT_FOUND",
//                    "로그인이 필요합니다.");
//        }
//
//        // Member 인증통과 안되면, INVALID_TOKEN , "Token이 유효하지 않습니다." 노출
//        Member member = validateMember(request);
//        if (null == member) {
//            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
//        }
//
//        /**
//         * 작성한 게시글들 목록 조회
//         */
//        List<Board> myBoardList = boardRepository.findAllByMember(member);
//        List<boardResponseDto> myBoardResponseDtoList = new ArrayList<>();
//
//        for(Board board : myBoardList) {
//            myBoardResponseDtoList.add(
//                    BoardResponseDto.builder()
//                            .id(board.getId())
//                            .category(board.getCategory())
//                            .nickname(board.getNickname())
//                            .createdAt(board.getCreatedAt())
//                            .modifiedAt(board.getModifiedAt())
//                            .build()
//            );
//        }
//
//
//
//        /**
//         * 내가 작성한 게시글 상세 조회
//         */
//        List<Board> myBoardList = boardRepository.findAllByMember(member);  // postRepository에서 findAllByMember로 가저온 정보를 myPostList에 list 형식으로 담아서
//        List<BoardResponseDto> myBoardResponseDtoList = new ArrayList<>(); // 배열의 크기를 알수 없을때 사용하면 좋은 ArrayList를 사용해서 myPostResponseDtoList 를 선언하고
//
//
////        List<Post> list = member.getLikePosts();
//        for (Board board : myBoardList) { // 반복문으로 돌면서 myPostResponseDtoList 에 .add 로 넣어준다.
//            myBoardResponseDtoList.add(
//                    BoardResponseDto.builder() // builder 패턴으로 필요한것을 선별적으로 넣어준다.
//                            .id(board.getId())         // id
//                            .title(board.getTitle())   // title
//                            .content(board.getContent())  // content
//                            .category(board.getCategory())
//                            .nickname(board.getNickname())
//                            .createdAt(board.getCreatedAt())  // 생성일
//                            .modifiedAt(board.getModifiedAt())  // 수정일
//                            // comment_cnt 추가해야함 cuz scheduller
//                            //comments 내역
//                            .build()
//            );
//        }
//
//        /** 작성한 댓글들 목록 조회*/
//        List<Comment> commentList = commentRepository.findAllByMember(member);
//        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
//
//        for(Comment comment : commentList ) {
//            CommentResponseDtoList.add(
//                    CommentResponseDto.builder()
//                            .id(comment.getPost())
//                            .nickname(comment.getNickname())
//                            .content(comment.getContent())  // content
//                            .createdAt(comment.getCreatedAt())  // 생성일
//                            .modifiedAt(comment.getModifiedAt())  // 수정일
//                            .build()
//            );
//        }
//
//        /** 내가 좋아요 누른 댓글 */
//        List<Comment> commentList = commentRepository.findAllByMember(member);
//        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
//
//        for(Comment comment : commentList ) {
//            CommentResponseDtoList.add(
//                    CommentResponseDto.builder()
//                            .id(comment.getPost())
//                            .nickname(comment.getNickname())
//                            .content(comment.getContent())  // content
//                            .createdAt(comment.getCreatedAt())  // 생성일
//                            .modifiedAt(comment.getModifiedAt())  // 수정일
//                            .build()
//            );
//        }
//
//
//        return ResponseDto.success(
//                MypageResponseDto.builder()
//                        .myPosts(myPostResponseDtoList)               // 내가 작성한 게시글
//                        .myComments(myCommentResponseDtoList)        // 내가 작성한 댓글 추가해야함 2022-10-20-오후 4시 35분
//                        .likeComments(likeCommentResponseDtoList)     // 좋아요 누른 댓글 .likeComments 추가함 2022-10-20-오후 4시00분
//                        .likePosts(likePostResponseDtoList)           //  좋아요 누른 게시글 추가함 2022-10-20-오후 4시 40분
//                        .build()
//        );
//    }
//
//    /**
//     * Refresh-Token으로 Member 인증
//     */
//    @Transactional
//    public Member validateMember(HttpServletRequest request) {
//        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
//            return null;
//        }
//        return tokenProvider.getMemberFromAuthentication();
//    }
//}
