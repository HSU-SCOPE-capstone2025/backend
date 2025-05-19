package com.scope.backend.scope_api.domain.frontend;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ad_price")
@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdPrice {

    @Id
    @Column(name = "influencer_num")
    private Long influencerNum;

    @OneToOne
    @MapsId
    @JoinColumn(name = "influencer_num")
    private Influencer influencer;

    @Column(name = "influencer_name")
    private String influencerName;

    @Column(name = "ad_price_insta")
    private String adPriceRangeInsta;

    @Column(name = "ad_price_tiktok")
    private String adPriceRangeTiktok;

    @Column(name = "ad_price_youtube")
    private String adPriceRangeYoutube;


}
