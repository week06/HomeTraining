package com.example.hometraing.repository;

import com.example.hometraing.domain.Member;
import com.example.hometraing.domain.ReComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReCommentRepository extends JpaRepository<ReComment, Long> {
    List<ReComment> findAllByCommentId(Long commentId);
    List<ReComment> findAllByMember(Member member);
    Optional<ReComment> findById(Long id);

    void delete(ReComment subComment);
}
