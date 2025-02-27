package org.example.expert.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Test
    void 적절한_데이터로_유저_회원가입_요청이_들어올_경우_성공() {
        //Given
        long userId = 1;
        SignupRequest signupRequest = new SignupRequest("user@example.com", "password", "USER");
        UserRole userRole = UserRole.of(signupRequest.getUserRole());
        String bearerToken = jwtUtil.createToken(userId, signupRequest.getEmail(), userRole);
        User user = User.toEntity(signupRequest.getEmail(), signupRequest.getPassword(), userRole);
        given(userRepository.save(any())).willReturn(user);

        //When
        SignupResponse signupResponse = authService.signup(signupRequest);

        //Then
        assertThat(bearerToken).isEqualTo(signupResponse.getBearerToken());
    }

    @Test
    void 이미_존재하는_이메일로_유저_회원가입_요청이_들어올_경우_실패() {
        //Given
        SignupRequest signupRequest = new SignupRequest("user@example.com", "password", "USER");
        given(userRepository.existsByEmail(any())).willReturn(true);

        //When & Then
        assertThatThrownBy(() -> authService.signup(signupRequest))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }

    @Test
    void 적절한_데이터로_유저_로그인_요청이_들어올_경우_성공() {
        //Given
        long userId = 1L;
        User user = User.toEntity("user@example.com", "password", UserRole.USER);
        String bearerToken = jwtUtil.createToken(userId, user.getEmail(), user.getUserRole());
        SigninRequest signinRequest = new SigninRequest("user@example.com", "password");

        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(any(), any())).willReturn(true);

        //When
        SigninResponse signinResponse = authService.signin(signinRequest);

        //Then
        assertThat(bearerToken).isEqualTo(signinResponse.getBearerToken());
    }

    @Test
    void 가입되지_않은_유저로_유저_로그인_요청이_들어올_경우_실패() {
        //Given
        SigninRequest signinRequest = new SigninRequest("user@example.com", "password");
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.signin(signinRequest))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("가입되지 않은 유저입니다.");
    }

    @Test
    void 비밀번호가_일치하지_않는_유저로_유저_로그인_요청이_들어올_경우_실패() {
        //Given
        User user = User.toEntity("user@example.com", "password", UserRole.USER);
        SigninRequest signinRequest = new SigninRequest("user@example.com", "password");

        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(any(), any())).willReturn(false);

        //When & Then
        assertThatThrownBy(() -> authService.signin(signinRequest))
                .isInstanceOf(AuthException.class)
                .hasMessage("잘못된 비밀번호입니다.");
    }
}