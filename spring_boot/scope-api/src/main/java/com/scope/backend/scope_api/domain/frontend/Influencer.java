package com.scope.backend.scope_api.domain.frontend;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "influencer")
public class Influencer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ElementCollection
    @CollectionTable(name = "influencer_tags", joinColumns = @JoinColumn(name = "influencer_id"))
    @Column(name = "tag")
    private List<String> tags;

    @ElementCollection
    @CollectionTable(name = "influencer_categories", joinColumns = @JoinColumn(name = "influencer_id"))
    @Column(name = "category")
    private List<String> categories;

    private long instaFollowers;
    private long tikFollowers;
    private long youFollowers;

    @OneToMany(mappedBy = "influencer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Instagram> instagrams;

    @OneToMany(mappedBy = "influencer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TikTok> tiktoks;

    @OneToMany(mappedBy = "influencer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<YouTube> youtubes;
}
