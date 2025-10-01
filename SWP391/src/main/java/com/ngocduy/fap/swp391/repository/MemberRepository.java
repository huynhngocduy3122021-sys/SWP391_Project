package com.ngocduy.fap.swp391.repository;

import com.ngocduy.fap.swp391.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}