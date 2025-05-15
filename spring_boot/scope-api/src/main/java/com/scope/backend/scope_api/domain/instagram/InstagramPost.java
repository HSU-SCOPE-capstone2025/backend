//package com.scope.backend.scope_api.domain.instagram;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "instagram_post", schema = "crawler")
//@Getter @Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@ToString
//public class InstagramPost {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "influencer_id", nullable = false)
//    private Long influencerId;
//
//    @Column(name = "url", nullable = false)
//    private String url;
//
//    @Column(name = "posted_date")
//    @JsonFormat(pattern = "yyyy-MM-dd")
//    private LocalDate postedDate;
//
//    @Column(name = "like_num")
//    private int likeNum;
//
//    @Column(name = "comment_num")
//    private int commentNum;
//
//    @Column(name = "created_at")
//    private LocalDate createdAt;
//}
