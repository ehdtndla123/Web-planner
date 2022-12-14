package com.note.plannerweb.member.controller;

import com.note.plannerweb.config.model.response.SingleResult;
import com.note.plannerweb.config.model.service.ResponseService;
import com.note.plannerweb.config.security.JwtProvider;
import com.note.plannerweb.member.dto.MemberDuplicateRequestDto;
import com.note.plannerweb.member.dto.MemberLoginRequestDto;
import com.note.plannerweb.member.dto.MemberLoginResponseDto;
import com.note.plannerweb.member.dto.MemberSignupRequestDto;
import com.note.plannerweb.member.service.MemberService;
import com.note.plannerweb.token.dto.TokenRequestDto;
import com.note.plannerweb.token.dto.tokenDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "0. SignUp_Login")
@RequiredArgsConstructor
@RestController
@RequestMapping(value="/api", produces = "application/json; charset=UTF8")
public class SignController {

    private final MemberService memberService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final ResponseService responseService;

    private final Logger logger= LoggerFactory.getLogger("GREEMA LOG");

    @ApiOperation(value ="로그인", notes = "이메일로 로그인을 합니다.")
    @PostMapping("/login")
    public SingleResult<tokenDto> login(@ApiParam(value = "로그인 요청 DTO",required = true)
                          @RequestBody MemberLoginRequestDto memberLoginRequestDto) {
//        MemberLoginResponseDto memberLoginResponseDto=this.memberService.login(memberLoginRequestDto);
//
//        return jwtProvider.createTokenDto(String.valueOf(memberLoginResponseDto.getMemberId()),memberLoginResponseDto.getRoles());
//    }
        tokenDto tokendto=memberService.login(memberLoginRequestDto);
        return responseService.getSingleResult(tokendto);
    }

    @ApiOperation(value = "회원가입",notes = "이메일로 회원가입을 합니다.")
    @PostMapping("/signup")
    public SingleResult<Long> signup(@ApiParam(value = "로그인 요청 DTO",required = true)
                           @RequestBody MemberSignupRequestDto memberSignupRequestDto){
        return this.responseService.getSingleResult(memberService.signup(memberSignupRequestDto));
    }

    @ApiOperation(value = "회원가입 이메일 중복 확인",notes = "이메일 중복을 확인합니다.")
    @PostMapping("/check/email")
    public SingleResult<Boolean> checkEmail(@ApiParam(value="이메일 요청",required = true) @RequestBody MemberDuplicateRequestDto memberDuplicateRequestDto){
        logger.info(memberDuplicateRequestDto.getEmail());
        return responseService.getSingleResult(memberService.checkEmail(memberDuplicateRequestDto.getEmail()));
    }

    @ApiOperation(value="엑세스, 리프레시 토큰 재발급"
    ,notes = "엑세스 토큰 만료시 회원 검증 후 리프레쉬 토큰을 검증해서 엑세스 토큰과 리프레시 토큰을 재발급합니다.")
    @PostMapping("/reissue")
    public SingleResult<tokenDto> reissue(@ApiParam(value="토큰 재발급 요청 DTO",required = true)
                            @RequestBody TokenRequestDto tokenRequestDto){
        return this.responseService.getSingleResult(memberService.reissue(tokenRequestDto));
    }
}
