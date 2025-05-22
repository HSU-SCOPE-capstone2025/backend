package com.scope.backend.scope_api.domain.frontend;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "instagram_comment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstagramComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fss")
    private Float fss;

    @Column(name = "comment")
    private String comment;

    @Column(name = "emotion")
    private String emotion;

    @Column(name = "topic")
    private String topic;

    @Column(name = "comment_date")
    private LocalDate commentDate;

    @Column(name = "cluster")
    private String cluster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_url", referencedColumnName = "post_url")  // 명시적으로 매핑
    private Instagram instagram;
}
