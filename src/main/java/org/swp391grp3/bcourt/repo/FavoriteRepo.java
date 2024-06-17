package org.swp391grp3.bcourt.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swp391grp3.bcourt.entities.Favorite;

public interface FavoriteRepo extends JpaRepository<Favorite, String> {
    Page<Favorite> findByUser_UserId(String userId, Pageable pageable);
}
