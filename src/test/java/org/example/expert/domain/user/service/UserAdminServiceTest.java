package org.example.expert.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceTest {

    @InjectMocks
    private UserAdminService userAdminService;

    @Mock
    private UserRepository userRepository;

    @Test
    void 유저_역할을_바꿀_시_성공적으로_바꾸어야_한다() {
        //Given
        long userId = 1;
        User user = User.toEntity("email", "password", UserRole.USER);
        UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest("ADMIN");
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        //When
        userAdminService.changeUserRole(userId, userRoleChangeRequest);
        //Then
        assertThat(UserRole.ADMIN).isEqualTo(user.getUserRole());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void 유저_역할을_바꿀_시_유저가_존재하지_않을_경우_예외_처리_한다() {
        //Given
        long userId = 1;
        UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest("ADMIN");
        given(userRepository.findById(userId)).willReturn(Optional.empty());
        //When & Then
        assertThatThrownBy(() -> userAdminService.changeUserRole(userId, userRoleChangeRequest))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("User not found");

    }
}