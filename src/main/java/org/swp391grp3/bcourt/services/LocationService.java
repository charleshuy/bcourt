package org.swp391grp3.bcourt.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.swp391grp3.bcourt.dto.CityDTO;
import org.swp391grp3.bcourt.dto.CourtDTO;
import org.swp391grp3.bcourt.dto.DistrictDTO;
import org.swp391grp3.bcourt.dto.LocationDTO;
import org.swp391grp3.bcourt.entities.City;
import org.swp391grp3.bcourt.entities.Court;
import org.swp391grp3.bcourt.entities.District;
import org.swp391grp3.bcourt.entities.Location;
import org.swp391grp3.bcourt.repo.CityRepo;
import org.swp391grp3.bcourt.repo.DistrictRepo;
import org.swp391grp3.bcourt.repo.LocationRepo;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Service
public class LocationService {
    private final CityRepo cityRepo;
    private final DistrictRepo districtRepo;
    private final LocationRepo locationRepo;
    private final ModelMapper modelMapper;

    public Page<LocationDTO> locationsDTOConverter(int page, int size, Page<Location> locationPage){
        return locationPage.map(loc -> modelMapper.map(loc, LocationDTO.class));
    }
    public Page<DistrictDTO> districtsDTOConverter(int page, int size, Page<District> districtPage){
        return districtPage.map(dis -> modelMapper.map(dis, DistrictDTO.class));
    }
    public Page<CityDTO> citiesDTOConverter(int page, int size, Page<City> cityPage){
        return cityPage.map(city -> modelMapper.map(city, CityDTO.class));
    }
    public List<CityDTO> citiesDTOList(List<City> cities){
        return cities.stream().map((element) -> modelMapper.map(element, CityDTO.class)).collect(Collectors.toList());
    }
    public List<DistrictDTO> districtDTOList(List<District> districts){
        return districts.stream().map((element) -> modelMapper.map(element, DistrictDTO.class)).collect(Collectors.toList());
    }

    public LocationDTO locationReturnToDTO(Location location){
        return modelMapper.map(location, LocationDTO.class);
    }
    public DistrictDTO distrctReturnToDTO(District district){
        return modelMapper.map(district, DistrictDTO.class);
    }
    public CityDTO cityReturnToDTO(City city){
        return modelMapper.map(city, CityDTO.class);
    }

    public Location createLocation(Location location){
        return locationRepo.save(location);
    }
    public District createDistrict(District district){
        return districtRepo.save(district);
    }
    public City createCity(City city){
        return cityRepo.save(city);
    }
    public Page<Location> getAllLocations(int page, int size) {
        Page<Location> locationPage = locationRepo.findAll(PageRequest.of(page, size));
        return locationPage;
    }
    public Page<District> getAllDistricts(int page, int size) {
        return districtRepo.findAll(PageRequest.of(page, size));
    }
    public Page<City> getAllCitiess(int page, int size) {
        return cityRepo.findAll(PageRequest.of(page, size));
    }
}
