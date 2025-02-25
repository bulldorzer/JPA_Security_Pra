package com.korea.shop.security;

import com.korea.shop.domain.Member;
import com.korea.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
* 사용자 인증 구현 클래스*/
@Service
@Log4j2
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberRepository.getWithRoles(email)
                .orElseThrow(()-> new UsernameNotFoundException("사용자를 찾을수 없음 "+email)); // null이 발생할 수 있음

        return new MemberDetails(member);
    }
}
