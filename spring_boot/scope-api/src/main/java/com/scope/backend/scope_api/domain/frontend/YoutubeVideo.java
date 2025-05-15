package com.scope.backend.scope_api.domain.frontend;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "youtube_video")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "like_count")
    private Integer likeCount;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "subscriber_count")
    private Integer subscriberCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "influencer_num")
    private Influencer influencer;

    @Column(name = "upload_date")
    private Date uploadDate;
}
