package com.ngocduy.fap.swp391.service;

import com.ngocduy.fap.swp391.entity.Member;
import com.ngocduy.fap.swp391.model.request.LoginRequest;
import com.ngocduy.fap.swp391.model.request.MemberRequest;
import com.ngocduy.fap.swp391.model.response.MemberResponse;
import com.ngocduy.fap.swp391.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService implements UserDetailsService {


    @Autowired
    MemberRepository memberRepository;

     @Autowired
    PasswordEncoder passwordEncoder;

     @Autowired
     AuthenticationManager authenticationManager;

     @Autowired
     ModelMapper modelMapper;

     @Autowired
     TokenService tokenService;

    public Member register(Member member) {
        // Xử lý logic cho register
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        //ma hoa mk
        //luu DB
        return memberRepository.save(member);
    }

    //login*
    public MemberResponse login(LoginRequest login) {

        // xử lí và xác thực tài khoản
        // b1 : lấy userName(Email) và password
        // b2 : tìm trong DB có account nào giống với UserName không(loadUserByUsername)
        // b3 : AuthenticationManager => so sanh tài khoảng và password dưới db <==> với password người dunùng nhập(authenticationManager)
          Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                  login.getEmail(),
                  login.getPassword()
          ));

          Member member = (Member) authentication.getPrincipal();
          /*
          // Nếu user đã bị xóa mềm thì không cho login
        if (member.isDeleted()) {
            throw new AuthenticationException("Account has been deleted or disabled");
        }
           */

          //member => memberResponse
          //==> maping bằng ModelMapper
          MemberResponse memberResponse = modelMapper.map(member, MemberResponse.class);
          String token = tokenService.generateToken(member);
          memberResponse.setToken(token);
          return memberResponse;


    }


    public List<Member> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        return members;
    }


    // Lấy user chưa bị xóa mềm
    public List<Member> getActiveMembers() {
        return memberRepository.findAllByDeletedFalse();
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return memberRepository.findMemberByEmail(email);
    }

    public Member getCurrentMember() {
        return (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    //update
    public Member updateMember(Long id, MemberRequest request) {
        return memberRepository.findById(id).map(existing -> {
            existing.setName(request.getName());
            existing.setEmail(request.getEmail());
            existing.setPhone(request.getPhone());
            existing.setAddress(request.getAddress());
            existing.setYearOfBirth(request.getYearOfBirth());
            existing.setSex(request.getSex());
            existing.setStatus(request.getStatus());
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                existing.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            return memberRepository.save(existing);
        }).orElse(null);
    }
    //delete
    public boolean deleteMember(Long id) {
        return memberRepository.findById(id).map(member -> {
            member.setDeleted(true);
            memberRepository.save(member);
            return true;
        }).orElse(false);
    }

}
