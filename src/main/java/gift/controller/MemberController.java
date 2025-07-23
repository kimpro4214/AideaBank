package gift.controller;

import gift.dto.LoginResponseDto;
import gift.dto.MemberRequestDto;
import gift.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody MemberRequestDto dto) {
        memberService.register(dto);
        return new ResponseEntity<>("회원가입 성공", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody MemberRequestDto dto) {
        String token = memberService.login(dto);
        LoginResponseDto response = new LoginResponseDto(token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
