package com.ngocduy.fap.swp391.service;

import com.ngocduy.fap.swp391.entity.Member;
import com.ngocduy.fap.swp391.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    @Autowired
    MemberRepository memberRepository;


    public Member register(Member member) {
        // Xử lý logic cho register

        //luu DB
        return memberRepository.save(member);
    }

    //login*




    public List<Member> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        return members;
    }

}
