package com.scope.backend.scope_api.domain.frontend;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "influencer")
@Data

@AllArgsConstructor
@NoArgsConstructor
public class Influencer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "influencer_num")
    private Long influencerNum;

    @Column(name = "influencer_name")
    private String name;

    @Column(name = "tags")
    private String tags;

    @Column(name = "categories")
    private String categories;

    @OneToMany(mappedBy = "influencer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Instagram> instagrams;

    @OneToMany(mappedBy = "influencer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tiktok> tiktoks;

    @OneToMany(mappedBy = "influencer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Youtube> youtubes;

    @OneToMany(mappedBy = "influencer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TotalFollower> totalFollowers;

    @OneToOne(mappedBy = "influencer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AdPrice adPrice;

    public String getInfluencerName() {
        return name;
    }
}
