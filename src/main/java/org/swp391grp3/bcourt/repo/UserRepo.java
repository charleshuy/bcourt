package org.swp391grp3.bcourt.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp391grp3.bcourt.entities.User;


@Repository
public interface UserRepo extends JpaRepository<User, String> {
    boolean existsByEmail(String email);
}
