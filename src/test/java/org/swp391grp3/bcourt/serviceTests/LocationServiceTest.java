package org.swp391grp3.bcourt.serviceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.swp391grp3.bcourt.dto.CityDTO;
import org.swp391grp3.bcourt.dto.DistrictDTO;
import org.swp391grp3.bcourt.entities.City;
import org.swp391grp3.bcourt.entities.District;
import org.swp391grp3.bcourt.repo.CityRepo;
import org.swp391grp3.bcourt.repo.DistrictRepo;
import org.swp391grp3.bcourt.services.LocationService;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

    @Mock
    private CityRepo cityRepo;

    @Mock
    private DistrictRepo districtRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private LocationService locationService;

    private City city;
    private CityDTO cityDTO;
    private District district;
    private DistrictDTO districtDTO;
    private Page<City> cityPage;
    private Page<District> districtPage;

    @BeforeEach
    public void setUp() {
        // Initialize city and district
        city = new City();
        city.setCityId("1");
        city.setCityName("Test City");

        district = new District();
        district.setDistrictId("1");
        district.setDistrictName("Test District");

        // Initialize CityDTO and DistrictDTO
        cityDTO = new CityDTO();
        cityDTO.setCityId("1");
        cityDTO.setCityName("Test City");

        districtDTO = new DistrictDTO();
        districtDTO.setDistrictId("1");
        districtDTO.setDistrictName("Test District");

        // Initialize pages
        List<City> cities = Arrays.asList(city);
        cityPage = new PageImpl<>(cities);

        List<District> districts = Arrays.asList(district);
        districtPage = new PageImpl<>(districts);
    }

    @Test
    public void testDistrictsDTOConverter() {
        when(modelMapper.map(any(District.class), eq(DistrictDTO.class))).thenReturn(districtDTO);

        Page<DistrictDTO> result = locationService.districtsDTOConverter(0, 10, districtPage);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(districtDTO.getDistrictId(), result.getContent().get(0).getDistrictId());
    }

    @Test
    public void testCitiesDTOConverter() {
        when(modelMapper.map(any(City.class), eq(CityDTO.class))).thenReturn(cityDTO);

        Page<CityDTO> result = locationService.citiesDTOConverter(0, 10, cityPage);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(cityDTO.getCityId(), result.getContent().get(0).getCityId());
    }

    @Test
    public void testCitiesDTOList() {
        when(modelMapper.map(any(City.class), eq(CityDTO.class))).thenReturn(cityDTO);

        List<CityDTO> result = locationService.citiesDTOList(cityPage.getContent());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(cityDTO.getCityId(), result.get(0).getCityId());
    }

    @Test
    public void testDistrictDTOList() {
        when(modelMapper.map(any(District.class), eq(DistrictDTO.class))).thenReturn(districtDTO);

        List<DistrictDTO> result = locationService.districtDTOList(districtPage.getContent());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(districtDTO.getDistrictId(), result.get(0).getDistrictId());
    }

    @Test
    public void testDistrictReturnToDTO() {
        when(modelMapper.map(any(District.class), eq(DistrictDTO.class))).thenReturn(districtDTO);

        DistrictDTO result = locationService.districtReturnToDTO(district);

        assertNotNull(result);
        assertEquals(districtDTO.getDistrictId(), result.getDistrictId());
    }

    @Test
    public void testCityReturnToDTO() {
        when(modelMapper.map(any(City.class), eq(CityDTO.class))).thenReturn(cityDTO);

        CityDTO result = locationService.cityReturnToDTO(city);

        assertNotNull(result);
        assertEquals(cityDTO.getCityId(), result.getCityId());
    }

    @Test
    public void testCreateDistrict() {
        when(districtRepo.save(any(District.class))).thenReturn(district);

        District result = locationService.createDistrict(district);

        assertNotNull(result);
        assertEquals(district.getDistrictId(), result.getDistrictId());
        verify(districtRepo, times(1)).save(any(District.class));
    }

    @Test
    public void testCreateCity() {
        when(cityRepo.save(any(City.class))).thenReturn(city);

        City result = locationService.createCity(city);

        assertNotNull(result);
        assertEquals(city.getCityId(), result.getCityId());
        verify(cityRepo, times(1)).save(any(City.class));
    }

    @Test
    public void testGetAllDistricts() {
        when(districtRepo.findAll(any(PageRequest.class))).thenReturn(districtPage);

        Page<District> result = locationService.getAllDistricts(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(district.getDistrictId(), result.getContent().get(0).getDistrictId());
    }

    @Test
    public void testGetAllCities() {
        when(cityRepo.findAll(any(PageRequest.class))).thenReturn(cityPage);

        Page<City> result = locationService.getAllCities(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(city.getCityId(), result.getContent().get(0).getCityId());
    }
}
