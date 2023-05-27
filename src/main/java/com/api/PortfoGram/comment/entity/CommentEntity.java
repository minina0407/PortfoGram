package com.api.PortfoGram.comment.entity;

import com.api.PortfoGram.post.entity.PostEntity;
import com.api.PortfoGram.user.entity.UserEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;



@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "comment")
@Getter
public class CommentEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @Column(nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;



    // constructors, getters, and setters
    @Builder
    public CommentEntity(Long id, UserEntity user, PostEntity post, String content, Date createdAt) {
        this.id = id;
        this.user = user;
        this.post = post;
        this.content = content;
        this.createdAt = createdAt;
    }
}
