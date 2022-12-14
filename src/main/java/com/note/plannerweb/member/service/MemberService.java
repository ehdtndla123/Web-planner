package com.note.plannerweb.member.service;

import com.note.plannerweb.config.security.JwtProvider;
import com.note.plannerweb.except.EmailLoginFailedCException;
import com.note.plannerweb.except.MemberNotFoundCException;
import com.note.plannerweb.except.SignUpFailedException;
import com.note.plannerweb.member.domain.Member;
import com.note.plannerweb.member.dto.*;
import com.note.plannerweb.member.repository.MemberRepository;
import com.note.plannerweb.token.domain.RefreshToken;
import com.note.plannerweb.token.dto.TokenRequestDto;
import com.note.plannerweb.token.dto.tokenDto;
import com.note.plannerweb.token.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;
    private final ModelMapper modelMapper;

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public MemberResponseDto findById(Long id) {
        Member member=this.memberRepository.findById(id).orElseThrow(MemberNotFoundCException::new);
        return new MemberResponseDto(member);
    }

    @Transactional
    public MemberResponseDto withdraw(String token){
        Member member=getMemberByToken(token);
        MemberResponseDto responseDto = new MemberResponseDto(member);
        memberRepository.delete(member);
        return responseDto;
    }

    @Transactional
    public MemberResponseDto updatePassword(String token, MemberUpdatePasswordDto memberUpdatePasswordDto) {
        Member memberByToken = getMemberByToken(token);
        memberByToken.setPassword(passwordEncoder.encode(memberUpdatePasswordDto.getPassword()));
        return new MemberResponseDto(memberByToken);
    }

    @Transactional
    public Boolean checkPassword(String token, MemberUpdatePasswordDto memberUpdatePasswordDto) {
        Member memberByToken = getMemberByToken(token);
        if (!passwordEncoder.matches(memberUpdatePasswordDto.getPassword(), memberByToken.getPassword())) {
            throw new EmailLoginFailedCException();
        }
        return true;
    }

    @Transactional
    public MemberResponseDto findByToken(String token){
        return new MemberResponseDto(getMemberByToken(token));
    }

    @Transactional
    public List<MemberResponseDto> getMemberList() throws MemberNotFoundCException {
       return this.memberRepository.findAll().stream()
                .map(o->modelMapper.map(o,MemberResponseDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public tokenDto login(MemberLoginRequestDto memberLoginRequestDto){
        // ?????? ?????? ??????????????? ??????
        Member member=this.memberRepository.findByEmail(memberLoginRequestDto.getEmail())
                .orElseThrow(EmailLoginFailedCException::new); //CEmailLoginFailedException
        //?????? ???????????? ?????? ?????? ??????
        if(!passwordEncoder.matches(memberLoginRequestDto.getPassword(),member.getPassword()))
            throw new EmailLoginFailedCException(); //CEmailLoginFailedException

        //AccessToken, RefreshToken ??????
        tokenDto tokendto=jwtProvider.createTokenDto(member.getId(),member.getRoles());
        //RefreshToken ??????
        RefreshToken refreshToken= RefreshToken.builder()
                .key(member.getId())
                .token(tokendto.getRefreshToken())
                .build();
        this.refreshTokenRepository.save(refreshToken);
        return tokendto;
    }

    @Transactional
    public Long signup(MemberSignupRequestDto memberSignupRequestDto){
        if(this.memberRepository.findByEmail(memberSignupRequestDto.getEmail()).isPresent())
            throw new SignUpFailedException(); // CEmailSignupFailedException()
        return this.memberRepository.save(memberSignupRequestDto.toEntity(passwordEncoder)).getId();
    }

    @Transactional
    public tokenDto reissue(TokenRequestDto tokenRequestDto){
        //????????? refresh token ??????
        if(!jwtProvider.validateToken(tokenRequestDto.getAccessToken())){
            throw new IllegalArgumentException(); //CrefreshTokenException()
        }

        //AccessToken ?????? Membername (pk) ????????????
        String accessToken = tokenRequestDto.getAccessToken();
        Authentication authentication= jwtProvider.getAuthentication(accessToken);

        // member pk??? ?????? ?????? / repo ??? ????????? Refresh token ??? ??????
        Member member=this.memberRepository.findById(Long.parseLong(authentication.getName()))
                .orElseThrow(IllegalArgumentException::new); //CUserNotFoundException
        RefreshToken refreshToken=this.refreshTokenRepository.findByKey(member.getId())
                .orElseThrow(IllegalArgumentException::new); //CUserNotFoundException

        //???????????? ?????? ????????? ??????
        if(!refreshToken.getToken().equals(tokenRequestDto.getRefreshToken()))
            throw new IllegalArgumentException(); //CRefreshTokenExceiption

        // AccessToken, RefreshToken ?????? ?????????, ???????????? ?????? ??????
        tokenDto newCreatedToken=this.jwtProvider.createTokenDto(member.getId(),member.getRoles());
        RefreshToken updateRefreshToken=refreshToken.updateToken(newCreatedToken.getRefreshToken());
        this.refreshTokenRepository.save(updateRefreshToken);

        return newCreatedToken;

    }
    @Transactional
    public Boolean checkEmail(String email){
        if(memberRepository.findByEmail(email).isPresent()){
            return false;
        }
        return true;
    }

    public Member getMemberByToken(String token){
        String userPk = this.jwtProvider.getUserPk(token);
        Long userLongPk=Long.parseLong(userPk);
        return this.memberRepository.findById(userLongPk).orElseThrow(MemberNotFoundCException::new);
    }
}
