package org.swp391grp3.bcourt.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.swp391grp3.bcourt.entities.User;


@Repository
public interface UserRepo extends JpaRepository<User, String> {
    boolean existsByEmail(String email);
    @Query("SELECT u FROM User u WHERE lower(u.name) like lower(concat('%', :userName, '%'))")
    Page<User> searchUserByName(String userName, Pageable pageable);
}
