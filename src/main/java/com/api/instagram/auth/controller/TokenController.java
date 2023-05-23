package com.api.instagram.auth.controller;


import com.api.instagram.auth.dto.Token;
import com.api.instagram.auth.service.TokenService;
import com.api.instagram.user.dto.AuthorizeUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class TokenController {
    private final TokenService tokenService;
    @PostMapping("/login")
    public ResponseEntity<Token> login(@Valid @RequestBody AuthorizeUser user) {
        Token token = tokenService.login(user);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }



}