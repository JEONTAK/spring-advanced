package org.example.expert.domain.todo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 수정일_기준_내림차순으로_모든_게시글을_조회할_수_있다() {
        //Given
        User user1 = userRepository.save(User.toEntity("user1@example.com", "password", UserRole.USER));
        User user2 = userRepository.save(User.toEntity("user2@example.com", "password", UserRole.USER));

        todoRepository.save(Todo.toEntity("Title1", "Contents1", "Sunny", user1));
        todoRepository.save(Todo.toEntity("Title2", "Contents2", "Windy", user1));
        todoRepository.save(Todo.toEntity("Title3", "Contents3", "Rain", user1));
        todoRepository.save(Todo.toEntity("Title4", "Contents4", "Cloud", user2));
        todoRepository.save(Todo.toEntity("Title5", "Contents5", "Rain", user2));
        Todo todo6 = todoRepository.save(Todo.toEntity("Title6", "Contents6", "Sunny", user2));

        Pageable pageable = PageRequest.of(0, 10);
        //When
        Page<Todo> todos = todoRepository.findAllByOrderByModifiedAtDesc(pageable);

        //Then
        assertThat(todos.getSize()).isEqualTo(10);
        assertThat(todos.getTotalElements()).isEqualTo(6);
        List<Todo> todoList = todos.getContent();
        //수정일 기준이므로 todo6 정보가 나와야 함
        assertThat(todoList.get(0)).isEqualTo(todo6);
        assertThat(todoList.get(0).getUser()).isEqualTo(user2);
    }

    @Test
    void 게시글_id값을_통해_조회할_수_있다() {
        //Given
        User user = userRepository.save(User.toEntity("user@example.com", "password", UserRole.USER));

        Todo todo = todoRepository.save(Todo.toEntity("Title", "Contents", "Sunny", user));

        //When
        Todo findTodo = todoRepository.findById(todo.getId())
                .orElseThrow(() -> new InvalidRequestException("Todo not found."));

        //Then
        assertThat(findTodo).isEqualTo(todo);
        assertThat(findTodo.getUser()).isEqualTo(user);
        assertThat(findTodo.getTitle()).isEqualTo("Title");
    }

}