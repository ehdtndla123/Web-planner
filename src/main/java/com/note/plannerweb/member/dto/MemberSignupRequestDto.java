package com.note.plannerweb.member.dto;

import com.note.plannerweb.member.domain.Member;
import com.note.plannerweb.note.domain.Note;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MemberSignupRequestDto {
    private String email;
    private String password;
    private String name;


    @Builder
    public MemberSignupRequestDto(String email,String password,String name){
        this.email=email;
        this.password=password;
        this.name=name;
    }

    public Member toEntity(PasswordEncoder passwordEncoder){
        return Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .roles(Collections.singletonList("ROLE_USER"))
                .build();
    }
}
