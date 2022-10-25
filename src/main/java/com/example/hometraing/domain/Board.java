package com.example.hometraing.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
//@EntityListeners(AuditingEntityListener.class)
@Entity
public class Board extends Timestamped{

    // 게시글 고유 id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    // 게시글 제목
    @Column(nullable = false)
    private String title;

    // 게시글 작성자 (작성할 떄는 현재 로그인한 유저명, 조회 요청을 하게 되면 해당 게시글을 작성한 유저명이 출력)
    @Column(nullable = false)
    private String author;

    // 게시글 내용
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // enum 타입의 카테고리
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    // 게시글을 작성한 유저의 id (author 칼럼과 같이 누가 작성한 게시글인지 파악할 수 있음)
    @JsonIgnore
    @JoinColumn(name = "memberid", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @CreatedBy
    private Long m_id;


    // 해당 게시글에 존재하는 모든 댓글들 (없을 수 있음)
    @OneToMany(mappedBy = "board",fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comment;

    // 해당 게시글에 존재하는 미디어 파일들 (없을 수 있음)
    @OneToMany(mappedBy = "board",fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Media> Media;




}
