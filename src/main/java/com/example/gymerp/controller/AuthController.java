package com.example.gymerp.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gymerp.dto.EmpDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/emp")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody EmpDto loginRequest, HttpServletRequest request) {
        log.info("로그인 요청: {}", loginRequest.getEmpEmail());

        try {
            // 1. 인증 토큰 생성
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmpEmail(), loginRequest.getPassword());

            // 2. AuthenticationManager 인증
            Authentication authentication = authenticationManager.authenticate(token);

            // 3. SecurityContext 저장
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            // 4. Session 저장
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            log.info("로그인 성공: {}", authentication.getName());

            // 5. [수정] 프론트엔드가 원하는 포맷으로 응답 반환
            // (CustomUserDetails로 캐스팅하여 정보 추출)
            com.example.gymerp.security.CustomUserDetails user = (com.example.gymerp.security.CustomUserDetails) authentication
                    .getPrincipal();

            java.util.Map<String, Object> res = new java.util.HashMap<>();
            res.put("empNum", user.getEmpNum());
            res.put("empName", user.getEmpName());
            res.put("email", user.getUsername());
            res.put("role", user.getRole()); // DB에 저장된 "ADMIN", "TRAINER" 등 원본 문자열
            res.put("sessionId", session.getId());

            return ResponseEntity.ok(res);

        } catch (AuthenticationException e) {
            log.error("로그인 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인 실패: 자격 증명에 실패하였습니다."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok().body(Map.of("message", "로그아웃 성공"));
    }
}
