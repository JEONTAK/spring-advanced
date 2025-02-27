package org.example.expert.domain.todo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @InjectMocks
    private TodoService todoService;

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private WeatherClient weatherClient;

    @Test
    void 게시글을_정상적으로_등록한다() {
        //Given
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("title", "contents");
        given(weatherClient.getTodayWeather()).willReturn("Sunny");
        Todo newTodo = Todo.toEntity("title", "contents", "Sunny", user);
        given(todoRepository.save(any())).willReturn(newTodo);

        //When
        TodoSaveResponse result = todoService.saveTodo(authUser, todoSaveRequest);

        //Then
        assertNotNull(result);
    }

    @Test
    void 게시글_전체_조회를_정상적으로_실행한다() {
        //Given
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        List<Todo> todoList = Arrays.asList(
                Todo.toEntity("title 1", "contents 1", "Sunny", user),
                Todo.toEntity("title 2", "contents 2", "Windy", user),
                Todo.toEntity("title 3", "contents 3", "Sunny", user),
                Todo.toEntity("title 4", "contents 4", "Cloudy", user),
                Todo.toEntity("title 5", "contents 5", "Rainy", user),
                Todo.toEntity("title 6", "contents 6", "Sunny", user)
        );
        Page<Todo> mockPage = new PageImpl<>(todoList, PageRequest.of(0, 10), todoList.size());
        given(todoRepository.findAllByOrderByModifiedAtDesc(PageRequest.of(0, 10)))
                .willReturn(mockPage);

        //When
        Page<TodoResponse> result = todoService.getTodos(1, 10);

        //Then
        assertNotNull(result);
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(6);
    }

    @Test
    void 게시글_단건_조회를_정상적으로_실행한다() {
        //Given
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        Todo todo = Todo.toEntity("title", "contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", 1L);
        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
        long findId = 1L;

        //When
        TodoResponse findTodo = todoService.getTodo(findId);

        //Then
        assertThat(findTodo.getId()).isEqualTo(todo.getId());
        assertThat(findTodo.getTitle()).isEqualTo(todo.getTitle());
    }

    @Test
    void 게시글_단건_조회시_해당_게시글이_없을_경우_예외_처리한다() {
        //Given
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        Todo todo = Todo.toEntity("title", "contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", 1L);
        given(todoRepository.findById(anyLong())).willReturn(Optional.empty());
        long findId = 2L;

        //When & Then
        assertThatThrownBy(() -> todoService.getTodo(findId))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Todo not found");
    }
}