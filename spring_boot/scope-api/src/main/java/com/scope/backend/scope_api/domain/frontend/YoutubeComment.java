package com.scope.backend.scope_api.domain.frontend;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "youtube_comment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YoutubeComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "comment")
    private String comment;

    @Column(name = "comment_date")
    private LocalDate commentDate;

    @Column(name = "emotion")
    private String emotion;

    @Column(name = "topic")
    private String topic;

    @Column(name = "cluster")
    private String cluster;

    @Column(name = "score")
    private int score;

    @Column(name = "fss")
    private Float ffs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_url")
    private Youtube youtube;
}
