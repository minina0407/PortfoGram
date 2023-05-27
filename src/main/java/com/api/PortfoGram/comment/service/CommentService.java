package com.api.PortfoGram.comment.service;

import com.api.PortfoGram.comment.dto.Comment;

import com.api.PortfoGram.comment.entity.CommentEntity;
import com.api.PortfoGram.comment.repository.CommentRepository;
import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.post.PostRepository;
import com.api.PortfoGram.post.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;


    @Transactional
    public Comment createComment(Long postId, Comment comment) {
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException("Post not found"));

        CommentEntity commentEntity = CommentEntity.builder()
                .post(postEntity)
                .content(comment.getContent())
                .build();

        CommentEntity savedCommentEntity = commentRepository.save(commentEntity);

        Comment savedComment = Comment.builder()
                .id(savedCommentEntity.getId())
                .content(savedCommentEntity.getContent())
                .build();

        return savedComment;
    }

    @Transactional
    public Comment updateComment(Long commentId, Comment comment) {
        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException("Comment not found"));

        CommentEntity updatedCommentEntity = commentRepository.save(commentEntity);

        Comment updatedComment = Comment.builder()
                .id(updatedCommentEntity.getId())
                .content(updatedCommentEntity.getContent())
                .build();

        return updatedComment;
    }

    @Transactional
    public void deleteComment(Long commentId) {
        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException("Comment not found"));

        commentRepository.delete(commentEntity);
    }

}