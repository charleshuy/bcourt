package org.swp391grp3.bcourt.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp391grp3.bcourt.entities.City;

@Repository
public interface CityRepo extends JpaRepository<City, String> {
}
