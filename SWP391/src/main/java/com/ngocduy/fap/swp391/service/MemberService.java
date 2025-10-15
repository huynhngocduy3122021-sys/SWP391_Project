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

    public MemberResponse register(Member member) {
        // Check if email already exists
        Member existingEmail = memberRepository.findMemberByEmail(member.getEmail());
        if (existingEmail != null) {
            throw new RuntimeException("Email already exists");
        }
        
        // Check if phone already exists
        Member existingPhone = memberRepository.findMemberByPhone(member.getPhone());
        if (existingPhone != null) {
            throw new RuntimeException("Phone number already exists");
        }
        
        // Xử lý logic cho register
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        //ma hoa mk
        //luu DB
        Member savedMember = memberRepository.save(member);
        // Convert Entity -> Response
        return convertToResponse(savedMember);
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

    /*
    public List<Member> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        return members;
    }
    */

    // Lấy user chưa bị xóa mềm
    public List<Member> getActiveMembers() {
        return memberRepository.findAllByDeletedFalse();
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return memberRepository.findMemberByEmail(email);
    }

    public MemberResponse getCurrentMember() {
        Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return convertToResponse(member);
    }

    // Get member by ID
    public MemberResponse getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        return convertToResponse(member);
    }

    //update
    public MemberResponse updateMember(Long id, MemberRequest request) {
        Member existing = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        
        // Check if email is being changed and if it already exists
        if (request.getEmail() != null && !request.getEmail().equals(existing.getEmail())) {
            Member existingEmail = memberRepository.findMemberByEmail(request.getEmail());
            if (existingEmail != null) {
                throw new RuntimeException("Email already exists");
            }
            existing.setEmail(request.getEmail());
        }
        
        // Check if phone is being changed and if it already exists
        if (request.getPhone() != null && !request.getPhone().equals(existing.getPhone())) {
            Member existingPhone = memberRepository.findMemberByPhone(request.getPhone());
            if (existingPhone != null) {
                throw new RuntimeException("Phone number already exists");
            }
            existing.setPhone(request.getPhone());
        }
        
        if (request.getName() != null) {
            existing.setName(request.getName());
        }
        if (request.getAddress() != null) {
            existing.setAddress(request.getAddress());
        }
        if (request.getYearOfBirth() != null) {
            existing.setYearOfBirth(request.getYearOfBirth());
        }
        if (request.getSex() != null) {
            existing.setSex(request.getSex());
        }
        if (request.getStatus() != null) {
            existing.setStatus(request.getStatus());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        Member updated = memberRepository.save(existing);
        return convertToResponse(updated);
    }
    //delete
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        member.setDeleted(true);
        memberRepository.save(member);
    }

    // Helper method: Convert Entity -> Response
    private MemberResponse convertToResponse(Member member) {
        MemberResponse response = new MemberResponse();
        response.setMemberId(member.getMemberId());
        response.setName(member.getName());
        response.setEmail(member.getEmail());
        response.setPhone(member.getPhone());
        response.setAddress(member.getAddress());
        response.setYearOfBirth(member.getYearOfBirth());
        response.setSex(member.getSex());
        response.setStatus(member.getStatus());
        return response;
    }

}
