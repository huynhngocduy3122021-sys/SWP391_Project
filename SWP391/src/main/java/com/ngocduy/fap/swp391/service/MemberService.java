package com.ngocduy.fap.swp391.service;

import com.ngocduy.fap.swp391.entity.Member;
import com.ngocduy.fap.swp391.exception.exceptions.DuplicateResourceException;
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
import org.springframework.web.servlet.resource.ResourceTransformer;

import java.util.List;

@Service
public class MemberService implements UserDetailsService {


     @Autowired
     private MemberRepository memberRepository;

     @Autowired
     private PasswordEncoder passwordEncoder;

     @Autowired
     private AuthenticationManager authenticationManager;

     @Autowired
     private ModelMapper modelMapper;

     @Autowired
     private TokenService tokenService;
    @Autowired
    private ResourceTransformer resourceTransformer;

    public Member register(MemberRequest member) {
        // Xử lý logic cho register
        if(memberRepository.findByPhone(member.getPhone()) != null){
            throw new DuplicateResourceException("Phone already exists");
        }
        if (memberRepository.findByEmail(member.getEmail()) != null){
            throw new DuplicateResourceException("Email already exists");
        }

        member.setPassword(passwordEncoder.encode(member.getPassword()));
        Member newMember = modelMapper.map(member, Member.class);

        //ma hoa mk
        //luu DB
        return memberRepository.save(newMember);
    }

    //login*
    public MemberResponse login(LoginRequest login) {

        // xử lí và xác thực tài khoản
        // b1 : lấy userName(Email) và password
        // b2 : tìm trong DB có account nào giống với UserName không(loadUserByUsername)
        // b3 : AuthenticationManager => so sanh tài khoảng và password dưới db <==> với password người dunùng nhập(authenticationManager)
//        Member member = new Member();
//        boolean checkAccount = member.
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

    /*
    // Get all active members
    public List<Member> getAllMembers() {
        return memberRepository.findAllByDeletedFalse();
    }
     */

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
            // Check email uniqueness if changed
            if (request.getEmail() != null && !request.getEmail().equals(existing.getEmail())) {
                if (memberRepository.findMemberByEmail(request.getEmail()) != null) {
                    throw new DuplicateResourceException("Email already in use");
                }
            }
            // Check phone uniqueness if changed
            if (request.getPhone() != null && !request.getPhone().equals(existing.getPhone())) {
                if (memberRepository.findByPhone(request.getPhone()) != null) {
                    throw new DuplicateResourceException("Phone already in use");
                }
            }
            // Use ModelMapper to map non-null fields from request to existing
            modelMapper.map(request, existing);

            // Handle password separately (only if provided)
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
