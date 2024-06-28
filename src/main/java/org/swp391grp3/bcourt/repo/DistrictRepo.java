package org.swp391grp3.bcourt.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp391grp3.bcourt.entities.District;

import java.util.List;

@Repository
public interface DistrictRepo extends JpaRepository<District, String> {
    List<District> findByCity_CityId(String cityId);
}
