package org.example.expert.domain.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void 이메일을_통해_유저를_조회할_수_있다() {
        //Given
        User user = userRepository.save(User.toEntity("user@example.com", "password", UserRole.USER));

        //When
        User findUser = userRepository.findByEmail("user@example.com")
                .orElseThrow(() -> new InvalidRequestException("User not found."));

        //Then
        assertThat(findUser).isEqualTo(user);
        assertThat(findUser.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void 이메일을_통해_유저가_존재하는지_확인할_수_있다() {
        //Given
        User user = userRepository.save(User.toEntity("user@example.com", "password", UserRole.USER));

        //When
        boolean isExist = userRepository.existsByEmail("user@example.com");

        //Then
        assertThat(isExist).isEqualTo(true);
    }
}