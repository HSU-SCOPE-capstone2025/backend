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
@Table(name = "instagram_comment")
public class InstagramComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "instagram_id")
    private Instagram instagram;

    private String commentText;
    private int ffs; // Follower Supporter Score
    private LocalDate commentDate;
}
