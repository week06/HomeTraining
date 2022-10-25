package com.example.hometraing.service;

import com.example.hometraing.controller.request.ReCommentRequestDto;
import com.example.hometraing.controller.response.ReCommentResponseDto;
import com.example.hometraing.controller.response.ResponseDto;
import com.example.hometraing.domain.Comment;
import com.example.hometraing.domain.Member;
import com.example.hometraing.domain.ReComment;
import com.example.hometraing.repository.ReCommentRepository;
import com.example.hometraing.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReCommentService {

    private final ReCommentRepository reCommentRepository;
    private final TokenProvider tokenProvider;
    private final CommentService commentService;

    //대댓글 생성
    @Transactional
    public ResponseDto<?> createReComment(ReCommentRequestDto requestDto, HttpServletRequest request
    ) {
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Comment comment = commentService.isPresentComment(requestDto.getCommentId());
        if (null == comment)
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");

        ReComment reComment = ReComment.builder()
                .commentId(comment.getId())
                .member(member)
                .content(requestDto.getContent())
                .build();
        reCommentRepository.save(reComment);
        return ResponseDto.success(
                ReCommentResponseDto.builder()
                        .id(reComment.getId())
                        .author(member.getNickname())
                        .content(reComment.getContent())
                        .createdAt(reComment.getCreatedAt())
                        .modifiedAt(reComment.getModifiedAt())
                        .build()
        );
    }

    //대댓글 조회
//    @Transactional(readOnly = true)
//    public ResponseDto<?> getAllReCommentByMember(HttpServletRequest request) {
//        Member member = validateMember(request);
//        if (null == member) {
//            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
//        }
//
//        List<ReComment> reCommentList = reCommentRepository.findAllByMember(member);
//        List<ReCommentResponseDto> reCommentResponseDtoList = new ArrayList<>();
//
//        for (ReComment reComment : reCommentList) {
//            reCommentResponseDtoList.add(
//                    ReCommentResponseDto.builder()
//                            .id(reComment.getId())
//                            .author(reComment.getMember().getNickname())
//                            .content(reComment.getContent())
//                            .createdAt(reComment.getCreatedAt())
//                            .modifiedAt(reComment.getModifiedAt())
//                            .build()
//            );
//        }
//        return ResponseDto.success(reCommentResponseDtoList);
//    }

    //대댓글 수정
    @Transactional
    public ResponseDto<?> updateReComment(Long id, ReCommentRequestDto requestDto,
                                          HttpServletRequest request) {
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Comment comment = commentService.isPresentComment(requestDto.getCommentId());
        if (null == comment)
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");

        ReComment reComment = isPresentReComment(id);
        if (null == reComment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
        }

        if (reComment.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        reComment.update(requestDto);
        return ResponseDto.success(
                ReCommentResponseDto.builder()
                        .id(reComment.getId())
                        .author(member.getNickname())
                        .content(reComment.getContent())
                        .createdAt(reComment.getCreatedAt())
                        .modifiedAt(reComment.getModifiedAt())
                        .build()
        );
    }

    //대댓글 삭제
    @Transactional
    public ResponseDto<?> deleteReComment(Long id,
                                          HttpServletRequest request) {
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Comment comment = commentService.isPresentComment(id);
        if (null == comment)
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");

        ReComment reComment = isPresentReComment(id);
        if (null == reComment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
        }

        if (reComment.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
        }

        reCommentRepository.delete(reComment);
        return ResponseDto.success("success");
    }

    @Transactional(readOnly = true)
    public ReComment isPresentReComment(Long id) {
        Optional<ReComment> optionalReComment = reCommentRepository.findById(id);
        return optionalReComment.orElse(null);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
