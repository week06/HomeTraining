package com.example.hometraing.domain;

import com.example.hometraing.controller.request.CommentRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table
@Entity
public class Comment extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    @JoinColumn(name = "boardId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Board board;

    @JoinColumn(name = "memberId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Member member;

//    private Long recomment;//댓글인지, 대댓글인지 구별하는 column(parent가 null이면 댓글, 아니면은 대댓글)

    // comment recomment 연관 관계 설정
//    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<SubComment> subComments = new ArrayList<>();

    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }

    public void update(CommentRequestDto requestDto) {
        this.content = requestDto.getContent();
    }
}
