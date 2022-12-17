package com.example.hometraing.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RefreshToken {

    @Column
    @Id
    private Long id;

    @JoinColumn(name = "memberid", nullable = false)
    @OneToOne()
    private Member member;

    @Column
    private String value;


}
