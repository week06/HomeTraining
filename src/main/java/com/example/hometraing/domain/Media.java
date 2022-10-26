package com.example.hometraing.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Media extends Timestamped{

    // 미디어 파일 고유 id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    // 미디어 파일 이름
    @Column(nullable = false)
    private String mediaName;

    // 미디어 파일 Url
    @Column(nullable = false)
    private String mediaUrl;

    // 연관관계 맺은 board
    @JsonIgnore
    @JoinColumn(name = "boardid", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;
}
