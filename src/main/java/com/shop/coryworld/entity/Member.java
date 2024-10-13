package com.shop.coryworld.entity;

import com.shop.coryworld.constant.Role;
import com.shop.coryworld.entity.base.BaseEntity;
import com.shop.coryworld.repository.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name="member")
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Column(unique=true)
    @Setter
    private String email;

    private String password;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    public Member(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        this.name = memberFormDto.getName();
        this.email = memberFormDto.getEmail();
        this.address = memberFormDto.getAddress();
        this.role = Role.ADMIN;
        this.password = passwordEncoder.encode(memberFormDto.getPassword());
    }

}
