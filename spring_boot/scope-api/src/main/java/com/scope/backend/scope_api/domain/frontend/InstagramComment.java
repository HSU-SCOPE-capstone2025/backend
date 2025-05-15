package com.scope.backend.scope_api.domain.frontend;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_url")
    private Instagram instagram;
}
