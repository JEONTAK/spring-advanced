package org.example.expert.domain.comment.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.example.expert.domain.comment.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentAdminServiceTest {

    @InjectMocks
    private CommentAdminService commentAdminService;

    @Mock
    private CommentRepository commentRepository;

    @Test
    void 댓글_삭제시_deleteById가_한_번_호출되어야_한다() {
        //Given
        long commentId = 1L;

        //When
        commentAdminService.deleteComment(commentId);

        //Then
        verify(commentRepository, times(1)).deleteById(commentId);
    }
}