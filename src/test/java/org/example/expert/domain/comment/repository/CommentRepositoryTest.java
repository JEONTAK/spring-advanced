package org.example.expert.domain.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Test
    void 특정_게시글_id에_해당하는_댓글과_작성자를_조회할_수_있다() {
        //Given
        User user1 = userRepository.save(User.toEntity("user1@example.com", "password", UserRole.USER));
        User user2 = userRepository.save(User.toEntity("user2@example.com", "password", UserRole.USER));

        Todo todo = todoRepository.save(Todo.toEntity("Title", "Contents", "Sunny", user1));

        Manager manager = managerRepository.save(Manager.toEntity(todo.getUser(), todo));

        Comment comment1 = commentRepository.save(Comment.toEntity("user1", user1, todo));
        Comment comment2 = commentRepository.save(Comment.toEntity("user2", user2, todo));

        //When
        List<Comment> comments = commentRepository.findByTodoIdWithUser(todo.getId());

        //Then
        assertThat(comments).hasSize(2);
        assertThat(comments.get(0)).isEqualTo(comment1);
        assertThat(comments.get(0).getContents()).isEqualTo("user1");
        assertThat(comments.get(0).getUser().getEmail()).isEqualTo("user1@example.com");
        assertThat(comments.get(1)).isEqualTo(comment2);
        assertThat(comments.get(1).getContents()).isEqualTo("user2");
        assertThat(comments.get(1).getUser().getEmail()).isEqualTo("user2@example.com");
    }

}