package com.api.PortfoGram.auth.controller;

import com.api.PortfoGram.auth.dto.Token;
import com.api.PortfoGram.auth.service.TokenService;
import com.api.PortfoGram.user.dto.AuthorizeUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "인증 API", description = "인증과 관련된 API")
public class TokenController {
    private final TokenService tokenService;

    @Operation(summary = "사용자 로그인", description = "사용자를 인증하고 토큰을 발급합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Token.class)))
    @PostMapping("/login")
    public ResponseEntity<Token> login(@Valid @RequestBody AuthorizeUser user) {
        Token token = tokenService.login(user);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @Operation(summary = "토큰 재발급", description = "유효한 토큰을 재발급합니다.")
    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공")
    @PostMapping("/reissue")
    public ResponseEntity<String> reissueToken(@Valid @RequestBody Token token) {
        tokenService.reissueToken(token);
        return new ResponseEntity<>("토큰 정보가 갱신되었습니다.",HttpStatus.OK);
    }
}
