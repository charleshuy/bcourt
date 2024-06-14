package org.swp391grp3.bcourt.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.swp391grp3.bcourt.entities.Court;
import org.swp391grp3.bcourt.entities.User;

import java.util.Optional;

@Repository
public interface CourtRepo extends JpaRepository<Court, String> {
    Optional<Court> findByCourtId(String courtId);
    Court findByUser_UserId(String userId);
    @Query("SELECT c FROM Court c WHERE lower(c.courtName) like lower(concat('%', :courtName, '%'))")
    Page<Court> findCourtByCourseName(String courtName, Pageable pageable);
}