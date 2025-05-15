package com.scope.backend.scope_api.domain.frontend;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "total_followers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TotalFollowers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "platform")
    private String platform;

    @Column(name = "subscriber_count")
    private Integer subscriberCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "influencer_num")
    private Influencer influencer;
}
