package com.scope.backend.scope_api.domain.frontend;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tiktok")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tiktok {
    @Id
    @Column(name = "video_url", length = 191)
    private String videoUrl;

    @Column(name = "description")
    private String description;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "comment_count")
    private Long commentCount;

    @Column(name = "share_count")
    private Long shareCount;

    @Column(name = "upload_date")
    private LocalDate uploadDate;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "follower_num")
    private Long followerNum;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_name")
    private String userName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "influencer_num")
    private Influencer influencer;

    @OneToMany(mappedBy = "tiktok", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TiktokComment> comments;
}
