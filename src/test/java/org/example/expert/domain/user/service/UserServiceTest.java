package org.example.expert.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void 유저를_성공적으로_조회할_수_있어야_한다() {
        //Given
        long userId = 1L;
        User user = User.toEntity("email", "password", UserRole.USER);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        //When
        UserResponse userResponse = userService.getUser(userId);

        //Then
        assertThat(userResponse.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void 유저_조회_시_유저가_존재하지_않는다면_예외_처리_한다() {
        //Given
        long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> userService.getUser(userId))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("User not found");
    }

    @Test
    void 비밀번호_변경_시_성공적으로_변경_되어야_한다() {
        //Given
        long userId = 1L;
        String oldPassword = passwordEncoder.encode("oldPassword");
        String newPassword = passwordEncoder.encode("newPassword");
        User user = User.toEntity("email", oldPassword, UserRole.USER);
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("oldPassword",
                "newPassword");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())).willReturn(true);
        given(passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())).willReturn(false);

        //When
        userService.changePassword(userId, userChangePasswordRequest);

        //Then
        assertThat(newPassword).isEqualTo(passwordEncoder.encode(user.getPassword()));
    }

    @Test
    void 비밀번호_변경_시_유저가_존재하지_않는다면_예외_처리_한다() {
        //Given
        long userId = 1L;
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("oldPassword",
                "newPassword");
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> userService.changePassword(userId, userChangePasswordRequest))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("User not found");
    }

    @Test
    void 비밀번호_변경_시_기존_비밀번호가__저장된_비밀번호와_다르다면_예외_처리_한다() {
        //Given
        long userId = 1L;
        User user = User.toEntity("email", "password", UserRole.USER);
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("oldPassword",
                "newPassword");
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())).willReturn(false);
        //When & Then
        assertThatThrownBy(() -> userService.changePassword(userId, userChangePasswordRequest))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("잘못된 비밀번호입니다.");
    }

    @Test
    void 비밀번호_변경_시_기존_비밀번호와_새_비밀번호가_같다면_예외_처리_한다() {
        //Given
        long userId = 1L;
        User user = User.toEntity("email", "password", UserRole.USER);
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("oldPassword",
                "newPassword");
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())).willReturn(true);
        given(passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())).willReturn(true);
        //When & Then
        assertThatThrownBy(() -> userService.changePassword(userId, userChangePasswordRequest))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
    }


}