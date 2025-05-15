package com.scope.backend.scope_api.domain.frontend;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "influencer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Influencer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "influencer_num")
    private Long id;

    @Column(name = "influencer_name")
    private String name;

    @Column(name = "categories")
    private String categories;

    @OneToMany(mappedBy = "influencer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TotalFollowers> followers;

    @OneToMany(mappedBy = "influencer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InstagramPost> instagramPosts;

    @OneToMany(mappedBy = "influencer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TiktokVideo> tiktokVideos;

    @OneToMany(mappedBy = "influencer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<YoutubeVideo> youtubeVideos;
}
