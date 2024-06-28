package org.swp391grp3.bcourt.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.swp391grp3.bcourt.dto.FavoriteDTO;
import org.swp391grp3.bcourt.dto.ReviewDTO;
import org.swp391grp3.bcourt.entities.Favorite;
import org.swp391grp3.bcourt.entities.Review;
import org.swp391grp3.bcourt.repo.FavoriteRepo;

@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Service
public class FavoriteService {
    private final FavoriteRepo favoriteRepo;
    private final ModelMapper modelMapper;


    public Page<FavoriteDTO> favoriteDTOConverter(int page, int size, Page<Favorite> favorites){
        return favorites.map(favorite -> modelMapper.map(favorite, FavoriteDTO.class));
    }

    public Favorite createFavorite(Favorite favo){
        return favoriteRepo.save(favo);
    }

    public Page<Favorite> getAllFavoritesByUserId(int page, int size, String userId){
        return favoriteRepo.findByUser_UserId(userId, PageRequest.of(page, size));
    }
    public void deleteFavoriteById(String id){
        Favorite favo = favoriteRepo.findById(id).orElseThrow(() -> new RuntimeException("Favorite not found"));
        favoriteRepo.delete(favo);
    }

}
