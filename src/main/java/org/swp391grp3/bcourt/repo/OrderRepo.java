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
    List<Order> findByUser_UserId(String userId);
    Page<Order> findByCourt_CourtId(String courtId, Pageable pageable);
    List<Order> findByCourt_CourtId(String courtId);
    @Query("SELECT o FROM Order o WHERE o.court.courtId = :courtId AND o.bookingDate = :bookingDate")
    List<Order> findByCourtAndBookingDate(@Param("courtId") String courtId, @Param("bookingDate") LocalDate bookingDate);
    @Query("SELECT o FROM Order o WHERE o.status IS NULL AND o.bookingDate < :currentDate")
    List<Order> findPendingOrdersPastBookingDate(@Param("currentDate") LocalDate currentDate);
    // New method for paginated results
    @Query("SELECT o FROM Order o WHERE o.court.courtId = :courtId AND o.bookingDate = :bookingDate")
    Page<Order> findByCourtAndBookingDate(@Param("courtId") String courtId, @Param("bookingDate") LocalDate bookingDate, Pageable pageable);
    void deleteByCourt_CourtId(String courtId);
    @Query("SELECT o FROM Order o WHERE o.bookingDate < :tenDaysAgo AND (o.status = true OR o.refund != true) AND o.method.methodName = 'E-Wallet' AND o.transferStatus != true")
    List<Order> findOrdersMoreThanTenDaysOld(@Param("tenDaysAgo") LocalDate tenDaysAgo);
}
