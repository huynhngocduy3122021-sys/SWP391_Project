package com.ngocduy.fap.swp391.service;

import com.ngocduy.fap.swp391.entity.Member;
import com.ngocduy.fap.swp391.enums.MemberStatus;
import com.ngocduy.fap.swp391.exception.exceptions.AuthenticationException;
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
import java.util.stream.Collectors;

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

    public MemberResponse register(MemberRequest member) {
        // Xử lý logic cho register
        if(memberRepository.findByPhone(member.getPhone()) != null){
            throw new DuplicateResourceException("Phone already exists");
        }
        if (memberRepository.findByEmail(member.getEmail()) != null){
            throw new DuplicateResourceException("Email already exists");
        }

        member.setPassword(passwordEncoder.encode(member.getPassword()));
        Member newMember = modelMapper.map(member, Member.class);
        Member savedMember = memberRepository.save(newMember);

        //ma hoa mk
        //luu DB
        return convertToResponse(savedMember);
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

          // Chặn đăng nhập nếu tài khoản INACTIVE
        if ("INACTIVE".equalsIgnoreCase(String.valueOf(member.getStatus()))) {
            throw new AuthenticationException("Account has been deleted or disabled");
        }



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

    // Lấy user có trạng thái ACTIVE
    public List<MemberResponse> getActiveMembers() {
        return memberRepository.findAllByStatus("ACTIVE")
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
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
            Member updated = memberRepository.save(existing);
            return convertToResponse(updated);
        }).orElse(null);
    }
    //delete
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        member.setStatus(MemberStatus.INACTIVE);
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
