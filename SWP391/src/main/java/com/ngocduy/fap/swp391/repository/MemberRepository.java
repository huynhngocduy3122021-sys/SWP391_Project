package com.ngocduy.fap.swp391.repository;

import com.ngocduy.fap.swp391.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // Tìm account thông qua email

    Member findMemberByEmail(String email);

    Member findMemberByMemberId(long memberId);

    //Chỉ lấy member chưa bị xóa
    Optional<Member> findByEmailAndDeletedFalse(String email);

    //  Lấy toàn bộ member chưa bị xóa
    List<Member> findAllByDeletedFalse();


}