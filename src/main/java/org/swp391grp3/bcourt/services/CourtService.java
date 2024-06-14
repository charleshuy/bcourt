package org.swp391grp3.bcourt.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;
    public Court createCourt(Court court){
        return courtRepo.save(court);
    }
    public Page<Court> getAllCourt(int page, int size) {
        Page<Court> courtPage = courtRepo.findAll(PageRequest.of(page, size, Sort.by("courtName")));
        return courtPage;
    }
    public Page<Court> searchCourtByName(int page, int size, String courtName){
        Page<Court> courtPage = courtRepo.findCourtByCourseName(courtName, PageRequest.of(page, size, Sort.by("courtName")));
        return courtPage;
    }
    public Page<CourtDTO> courtReturnToDTO(int page, int size, Page<Court> courtPage){
        return courtPage.map(court -> modelMapper.map(court, CourtDTO.class));
    }

    public Court getCourtByUserId(String userId){
        return courtRepo.findByUser_UserId(userId);
    }
    public CourtDTO courtReturnToDTO(Court court){
        return modelMapper.map(court, CourtDTO.class);
    }

    public void deleteCourt(Court court){
        courtRepo.delete(court);
    }

}
