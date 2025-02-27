package org.example.expert.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TodoService todoService;
    @InjectMocks
    private CommentService commentService;

    @Test
    public void 댓글_등록_중_할일을_찾지_못해_에러가_발생한다() {
        //Given
        long todoId = 1;
        CommentSaveRequest request = new CommentSaveRequest("contents");
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);

        given(todoService.findById(anyLong())).willThrow(new InvalidRequestException("Todo not found"));

        //When & Then
        assertThatThrownBy(() -> commentService.saveComment(authUser, todoId, request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Todo not found");
    }

    @Test
    public void 댓글을_정상적으로_등록한다() {
        // given
        long todoId = 1;
        CommentSaveRequest request = new CommentSaveRequest("contents");
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        Todo todo = Todo.toEntity("title", "title", "contents", user);
        Comment comment = Comment.toEntity(request.getContents(), user, todo);

        given(todoService.findById(anyLong())).willReturn(todo);
        given(commentRepository.save(any())).willReturn(comment);

        // when
        CommentSaveResponse result = commentService.saveComment(authUser, todoId, request);

        // then
        assertNotNull(result);
    }

    @Test
    void 특정_게시글의_댓글_조회시_정상적으로_조회_되어야_한다() {
        //Given
        long todoId = 1;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        Todo todo = Todo.toEntity("title", "title", "Sunny", user);
        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CommentSaveRequest request = new CommentSaveRequest("contents" + i);
            comments.add(Comment.toEntity(request.getContents(), user, todo));
        }
        given(commentRepository.findByTodoIdWithUser(any())).willReturn(comments);

        //When
        List<CommentResponse> result = commentService.getComments(todoId);

        //Then
        assertNotNull(result);
        assertThat(result.size()).isEqualTo(10);
    }
}
