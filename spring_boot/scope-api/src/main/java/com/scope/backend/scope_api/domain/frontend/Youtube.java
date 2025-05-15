package com.scope.backend.scope_api.domain.frontend;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "youtube")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Youtube {
    @Id
    @Column(name = "video_url", length = 191)
    private String videoUrl;

    @Column(name = "upload_date")
    private LocalDate uploadDate;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "comment_count")
    private Long commentCount;

    @Column(name = "subscriber_count")
    private Long subscriberCount;

    @Column(name = "channel_url")
    private String channelUrl;

    @Column(name = "channel_title")
    private String channelTitle;

    @Column(name = "channel_description")
    private String channelDescription;

    @Column(name = "topic_categories")
    private String topicCategories;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "tags")
    private String tags;

    @Column(name = "thumbnails")
    private String thumbnails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "influencer_num")
    private Influencer influencer;

    @OneToMany(mappedBy = "youtube", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<YoutubeComment> comments;
}
