package com.scope.backend.scope_api.repository.instagram;

import com.scope.backend.scope_api.domain.instagram.InstagramFollower;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstagramFollowerRepository extends JpaRepository<InstagramFollower, Long> {
}