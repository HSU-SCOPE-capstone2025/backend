package com.scope.backend.scope_api.domain.instagram;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "instagram_follower",  schema = "crawler")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InstagramFollower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "insta_name")
    private String instaName;

    @Column(name = "influencer_id")
    private Long influencerId; // 외래키지만 지금은 단순 숫자만 저장

    @Column(name = "follower_num")
    private Integer followerNum;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
