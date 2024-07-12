package org.swp391grp3.bcourt.serviceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.swp391grp3.bcourt.dto.FavoriteDTO;
import org.swp391grp3.bcourt.entities.Favorite;
import org.swp391grp3.bcourt.repo.FavoriteRepo;
import org.swp391grp3.bcourt.services.FavoriteService;
import java.util.Arrays;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FavoriteServiceTest {

    @Mock
    private FavoriteRepo favoriteRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private FavoriteService favoriteService;

    private Favorite favorite;
    private FavoriteDTO favoriteDTO;
    private Page<Favorite> favoritePage;

    @BeforeEach
    public void setUp() {
        // Initialize Favorite
        favorite = new Favorite();
        favorite.setSaveId("1");

        // Initialize FavoriteDTO
        favoriteDTO = new FavoriteDTO();
        favoriteDTO.setSaveId("1");

        // Set up list of favorites
        favoritePage = new PageImpl<>(Arrays.asList(favorite));
    }

    @Test
    public void testFavoriteDTOConverter() {
        when(modelMapper.map(any(Favorite.class), eq(FavoriteDTO.class))).thenReturn(favoriteDTO);

        Page<FavoriteDTO> result = favoriteService.favoriteDTOConverter(0, 10, favoritePage);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("1", result.getContent().get(0).getSaveId());
        verify(modelMapper, times(1)).map(any(Favorite.class), eq(FavoriteDTO.class));
    }

    @Test
    public void testCreateFavorite() {
        when(favoriteRepo.save(any(Favorite.class))).thenReturn(favorite);

        Favorite createdFavorite = favoriteService.createFavorite(favorite);

        assertNotNull(createdFavorite);
        assertEquals("1", createdFavorite.getSaveId());
        verify(favoriteRepo, times(1)).save(favorite);
    }

    @Test
    public void testGetAllFavoritesByUserId() {
        when(favoriteRepo.findByUser_UserId(anyString(), any(PageRequest.class))).thenReturn(favoritePage);

        Page<Favorite> result = favoriteService.getAllFavoritesByUserId(0, 10, "1");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(favoriteRepo, times(1)).findByUser_UserId(anyString(), any(PageRequest.class));
    }

    @Test
    public void testDeleteFavoriteById() {
        when(favoriteRepo.findById(anyString())).thenReturn(Optional.of(favorite));
        doNothing().when(favoriteRepo).delete(any(Favorite.class));

        favoriteService.deleteFavoriteById("1");

        verify(favoriteRepo, times(1)).findById("1");
        verify(favoriteRepo, times(1)).delete(favorite);
    }
}

