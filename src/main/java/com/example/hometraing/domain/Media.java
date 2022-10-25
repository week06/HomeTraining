package com.example.hometraing.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.File;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Media extends Timestamped{

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String mediaName;

    @Column(nullable = false)
    private String mediaUrl;
    @JsonIgnore
    @JoinColumn(name = "boardid", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;
}
