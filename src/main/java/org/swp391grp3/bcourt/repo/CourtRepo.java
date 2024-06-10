package org.swp391grp3.bcourt.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp391grp3.bcourt.entities.Court;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourtRepo extends JpaRepository<Court, String> {
    Optional<Court> findByCourtId(UUID courtId);
}