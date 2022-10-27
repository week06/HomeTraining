package com.example.hometraing.domain;

import com.example.hometraing.controller.request.ReCommentRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ReComment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //대댓글을 작성할 멤버 id
    @JoinColumn(name = "memberId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    //댓글 id
    private Long commentId;

    @Column(nullable = false)
    private String content;

    public void update(ReCommentRequestDto requestDto) {
        this.content = requestDto.getContent();
    }
}
