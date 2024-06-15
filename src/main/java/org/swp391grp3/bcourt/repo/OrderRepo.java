package org.swp391grp3.bcourt.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.swp391grp3.bcourt.entities.Order;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepo extends JpaRepository<Order, String> {
    Page<Order> findByUser_UserId(String userId, Pageable pageable);
    @Query("SELECT o FROM Order o WHERE o.court.courtId = :courtId AND o.date = :date")
    List<Order> findByCourtAndDate(@Param("courtId") String courtId, @Param("date") LocalDate date);
}
