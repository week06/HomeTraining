package com.example.hometraing.repository;

import com.example.hometraing.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Member findByMemberidAndPassword(String memberid, String password);

    Member findByMemberid(String memberid);
}