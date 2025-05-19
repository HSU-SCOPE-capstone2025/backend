package com.scope.backend.scope_api.domain.frontend;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "instagram")
@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Instagram {
    @Id
    @Column(name = "post_url")
    private String postUrl;

    @Column(name = "post_date")
    private LocalDate postDate;

    @Column(name = "like_count")
    private int likeCount;

    @Column(name = "user_id")
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "influencer_num")
    private Influencer influencer;



}
