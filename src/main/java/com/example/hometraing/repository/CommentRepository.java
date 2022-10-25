package com.example.hometraing.repository;

import com.example.hometraing.domain.Board;
import com.example.hometraing.domain.Comment;
import com.example.hometraing.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository  extends JpaRepository<Comment, String>{
    Optional<Comment> findById(Long id);

    List<Comment> findAllByBoardId(Long boardId);
//    Comment findByBoardIdAndCommentId(Long postingId, Long commentId);

}
