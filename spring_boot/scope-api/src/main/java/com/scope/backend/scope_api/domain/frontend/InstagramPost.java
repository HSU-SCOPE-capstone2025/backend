package com.scope.backend.scope_api.domain.frontend;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "instagram_post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InstagramPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "like_count")
    private Integer likeCount;

    @Column(name = "comment_count")
    private Integer commentCount;

    @Column(name = "follower_num")
    private Integer followerNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "influencer_num")
    private Influencer influencer;
}
