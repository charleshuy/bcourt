package org.swp391grp3.bcourt.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.swp391grp3.bcourt.DTO.CourtDTO;
import org.swp391grp3.bcourt.entities.Court;
import org.swp391grp3.bcourt.repo.CourtRepo;

@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Service
public class CourtService {
    private final CourtRepo courtRepo;
    public Court createCourt(Court court){
        return courtRepo.save(court);
    }
    public Page<CourtDTO> getAllCourt(int page, int size) {
        Page<Court> courtPage = courtRepo.findAll(PageRequest.of(page, size, Sort.by("courtName")));
        return courtPage.map(this::convertToDTO);
    }
    public Page<Court> getAllCourtByUserId(int page, int size, String userId){
        return courtRepo.findByUser_UserId(userId, PageRequest.of(page, size, Sort.by("courtName")));
    }
    public CourtDTO convertToDTO(Court court) {
        CourtDTO dto = new CourtDTO();
        dto.setCourtId(court.getCourtId());
        dto.setCourtName(court.getCourtName());
        dto.setUserName(court.getUser().getName());
        // Set other fields as needed
        return dto;
    }
    public void deleteCourt(Court court){
        courtRepo.delete(court);
    }

}
