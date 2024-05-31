package com.se.its.domain.member.domain.respository;

import com.se.its.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findBySignIdAndIsDeletedFalse(String signId);

    List<Member> findByIsDeletedIsFalse();

}
