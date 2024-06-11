package org.swp391grp3.bcourt.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp391grp3.bcourt.entities.Court;

import java.util.Optional;

@Repository
public interface CourtRepo extends JpaRepository<Court, String> {
    Optional<Court> findByCourtId(String courtId);
    Page<Court> findByUser_UserId(String userId, Pageable pageable);
}