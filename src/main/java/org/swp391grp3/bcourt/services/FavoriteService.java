package org.swp391grp3.bcourt.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.swp391grp3.bcourt.entities.Favorite;
import org.swp391grp3.bcourt.repo.FavoriteRepo;

@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Service
public class FavoriteService {
    private final FavoriteRepo favoriteRepo;
    public Favorite createFavorite(Favorite favo){
        return favoriteRepo.save(favo);
    }
    private Page<Favorite> getFavoritesByUserId(int page, int size, String userId){
        return favoriteRepo.findByUser_UserId(userId, PageRequest.of(page, size));
    }

}
