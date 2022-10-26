package com.example.hometraing.repository;

import com.example.hometraing.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>{
    Optional<Comment> findById(@Param("id") Long id);


//    List<Comment> findByBoard(Board board);
//    List<Comment> findAllByMember(Member member);
//    int countAllByBoard(Board board);
//
//    Optional<Comment> findById(Long id);


    List<Comment> findAllByBoardId(@Param("boardId") Long boardId);
//    Comment findByBoardIdAndCommentId(Long postingId, Long commentId);

}
