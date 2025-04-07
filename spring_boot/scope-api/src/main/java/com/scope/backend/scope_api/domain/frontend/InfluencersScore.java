package com.scope.backend.scope_api.domain.frontend;


import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class InfluencersScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int followers;
    private int ffs;
    private String followersFeature;
    private int averageViews;
    private int averageComments;
    private int averageLikes;

    @Column(name = "date")
    private LocalDate date;
}
