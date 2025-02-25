package com.korea.shop.security;

import com.korea.shop.domain.Address;
import com.korea.shop.domain.Member;
import com.korea.shop.dto.MemberDTO;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/* 사용자의 정보를 저장하는 클래스 */
@Getter
public class MemberDetails implements UserDetails {

    /* 토큰에 저장된 사용자 정보 */
    /*
    * 아이디(이메일), 패스워드, 이름, 주소, 권한
    */
    private final String email;
    private final String password;
    private final String name;
    private final Address address;
    private final List<GrantedAuthority> authorities;

    public MemberDetails(Member member){
        this.email = member.getEmail();
        this.password = member.getPw();
        this.name = member.getName();
        this.address = member.getAddress();
        // SimpleGrantedAuthority 사용자 권한을 문자열로 저장하는 클래스
        this.authorities = member.getMemberRoleList().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_"+role))
                .collect(Collectors.toList());
    }

    // GrantedAuthority 데이터 형태는 ROLE_USER, ROLE_ADMIN
    public MemberDetails(String email, String password, String name, Address address, List<GrantedAuthority> authorities) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.address = address;
        this.authorities = authorities;
    }

    /* MemberDetails -> MemberDTO로 변환하는 메서드*/
    public MemberDTO toMemberDTO(){
        return new MemberDTO(this.email, this.password, this.name, this.address,
                this.authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(role -> role.replace("ROLE_",""))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities;  }

    @Override
    public String getPassword() {  return password;   }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
