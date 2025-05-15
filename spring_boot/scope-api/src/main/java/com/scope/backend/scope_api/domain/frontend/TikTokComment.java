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
@Table(name = "tiktok_comment")
public class TikTokComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tiktok_id")
    private TikTok tiktok;

    private String commentText;
    private int ffs; // Follower Supporter Score
    private LocalDate commentDate;
}

