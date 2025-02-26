package org.example.expert.domain.manager.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ManagerRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Test
    void 특정_게시글_id에_해당하는_매니저_목록_조회에_성공한다() {
        //Given
        User user1 = userRepository.save(User.toEntity("user1@example.com", "password", UserRole.USER));

        Todo todo = todoRepository.save(Todo.toEntity("Title", "Contents", "Sunny", user1));

        Manager manager = managerRepository.findById(1L)
                .orElseThrow(() -> new InvalidRequestException("담당자를 등록하려고 하는 유저가 일정을 만든 유저가 유효하지 않습니다."));

        //When
        List<Manager> managers = managerRepository.findByTodoIdWithUser(todo.getId());

        //Then
        assertThat(managers).hasSize(1);
        assertThat(managers.get(0)).isEqualTo(manager);
        assertThat(managers.get(0).getTodo()).isEqualTo(todo);
        assertThat(managers.get(0).getUser()).isEqualTo(user1);
    }

}