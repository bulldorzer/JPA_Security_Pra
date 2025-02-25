package com.korea.shop.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder // 객체 생성 + 초기화
@ToString(exclude = "memberRoleList")
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id") // 컬럼명 변경
    private Long id;

    /*
     * 아이디(이메일), 패스워드, 이름, 주소, 권한
     */
    private String name;
    private String email;
    private String pw;

    @Embedded // 값 타입 포함
    private Address address;

    @ElementCollection(fetch = FetchType.LAZY) // 지연로딩 - 필요시 조인하고 있는 추가 데이터 생성
    @Builder.Default // 빌더 사용시 new 객체 생성 가능 -> 선언안할시 new 객체생성  null 초기화
    private List<MemberRole> memberRoleList = new ArrayList<>();

    public void addRole(MemberRole memberRole){ memberRoleList.add(memberRole);}

    public void clearRole(){memberRoleList.clear();}

    public void changeName(String name){ this.name = name;}
    public void changeEmail(String email){this.email = email;}
    public void changePw(String pw){this.pw = pw;}
    public void changeAddress(Address address){ this.address = address;}
    


}
