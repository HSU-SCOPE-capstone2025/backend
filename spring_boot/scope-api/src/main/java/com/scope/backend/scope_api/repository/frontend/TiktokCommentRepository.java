package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.TiktokComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TiktokCommentRepository extends JpaRepository<TiktokComment, Long> {
}
