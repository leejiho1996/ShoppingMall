package com.shop.coryworld.service;

import com.shop.coryworld.auth.PrincipalDetails;
import com.shop.coryworld.entity.Member;
import com.shop.coryworld.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            throw new UsernameNotFoundException(email);
        }

        return PrincipalDetails.builder()
                .id(member.getId())
                .username(member.getEmail())
                .password(member.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(member.getRole().toString())))
                .build();
    }

    @Transactional
    public Member saveMember(Member member) {
        validateDuplicate(member);
        return memberRepository.save(member);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId).orElse(null);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    private void validateDuplicate(Member member) {
        Member result = memberRepository.findByEmail(member.getEmail());
        if (result != null) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }


}
