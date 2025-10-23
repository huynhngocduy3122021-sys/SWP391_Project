package com.ngocduy.fap.swp391.repository;

import com.ngocduy.fap.swp391.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // Tìm account thông qua email

    Member findMemberByEmail(String email);

    Member findMemberByMemberId(long memberId);

    Member findByPhone(String phone);

    Member findByEmail(String email);
    /*Chỉ lấy member chưa bị xóa
    Optional<Member> findByEmailAndDeletedFalse(String email);*/

    //  Lấy toàn bộ member chưa bị xóa
    List<Member> findAllByDeletedFalse();

}