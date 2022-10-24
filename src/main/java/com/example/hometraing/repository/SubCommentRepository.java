package com.example.hometraing.repository;

import com.example.hometraing.domain.Member;
import com.example.hometraing.domain.SubComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubCommentRepository extends JpaRepository<SubComment, Long> {
    List<SubComment> findAllByCommentId(Long commentId);
    List<SubComment> findAllByMember(Member member);
    Optional<SubComment> findById(Long id);

    void delete(SubComment subComment);
}
