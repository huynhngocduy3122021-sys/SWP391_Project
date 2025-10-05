package com.ngocduy.fap.swp391.service;

import com.ngocduy.fap.swp391.entity.Member;
import com.ngocduy.fap.swp391.model.request.LoginRequest;
import com.ngocduy.fap.swp391.model.response.MemberResponse;
import com.ngocduy.fap.swp391.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService implements UserDetailsService {


    @Autowired
    MemberRepository memberRepository;

     @Autowired
    PasswordEncoder passwordEncoder ;

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
        try{
          Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));

          Member member = (Member) authentication.getPrincipal();
          //==> maping bằng ModelMapper
        MemberResponse memberResponse = modelMapper.map(member, MemberResponse.class);
        String token = tokenService.generateToken(member);
        memberResponse.setToken(token);
        return memberResponse ;

        }catch(Exception e){
            throw new BadCredentialsException("Invalid email or password");
        }

    }





    public List<Member> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        return members;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return memberRepository.findMemberByEmail(email);
    }

}
