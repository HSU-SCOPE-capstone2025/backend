package com.scope.backend.scope_api.domain.frontend;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "youtube_comment")
public class YouTubeComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "youtube_id")
    private YouTube youtube;

    private String commentText;
    private int ffs; // Follower Supporter Score
    private LocalDate commentDate;
}

