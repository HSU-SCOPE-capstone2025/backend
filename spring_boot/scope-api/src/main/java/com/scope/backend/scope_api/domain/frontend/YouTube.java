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
@Table(name = "youtube")
public class YouTube {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "influencer_id")
    private Influencer influencer;

    private String videoUrl;
    private LocalDate uploadDate;
    private long views;
    private long likes;
}
