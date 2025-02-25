package com.korea.shop.dto;

import com.korea.shop.domain.Address;
import com.korea.shop.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    /*
     * 아이디(이메일), 패스워드, 이름, 주소, 권한
     */
    private Long id;
    private String name;
    private String email;
    private String pw; // 추가
    private Address address;

    private List<String> roleNames =  new ArrayList<>(); // 권한 리스트

    public MemberDTO(Member member){
        this.id = member.getId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.pw = member.getPw();
        this.address = member.getAddress();
        this.roleNames = (member.getMemberRoleList()==null)
                ? new ArrayList<>()
                : member.getMemberRoleList().stream()
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public MemberDTO(String email, String pw, String name, Address address, List<String> roleNames){
        this.email = email;
        this.pw = pw;
        this.name = name;
        this.address = address;
        this.roleNames = roleNames;
    }

    /*
    * dto에 유일하게 포함할수 있는 메서드
    * 1) 유틸리티 메서드 (문자열 포매팅, 날짜 변환) = 간단환 데이터를 변환하는 기능
    * 2) 정적 팩토리 메소드 : 엔티티 -> DTO 변환하는 메서드, 객체 생성을 돕는 메서드
    */

    // DTO 데이터를 MAP형태로 사용할수 있도록 변환하는 메서드, 토큰 변환에 필요함
    // 토글변환 과정 DTP -> MAP -> JSON 형식
    public Map<String,Object> getClaims(){

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("email",email);
        dataMap.put("pw",pw);
        dataMap.put("name",name);
        dataMap.put("roleNames",roleNames);

        return dataMap;
    }

}
