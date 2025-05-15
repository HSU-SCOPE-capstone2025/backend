package com.scope.backend.scope_api.domain.frontend;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "total_follower")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalFollower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "platform")
    private String platform;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "subscriber_count")
    private Long subscriberCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "influencer_num")
    private Influencer influencer;
}
