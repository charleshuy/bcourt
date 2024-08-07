package org.swp391grp3.bcourt.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.swp391grp3.bcourt.dto.CourtDTO;
import org.swp391grp3.bcourt.entities.Court;
import org.swp391grp3.bcourt.entities.FileData;
import org.swp391grp3.bcourt.entities.Order;
import org.swp391grp3.bcourt.entities.User;
import org.swp391grp3.bcourt.repo.CourtRepo;
import org.swp391grp3.bcourt.repo.FileRepo;
import org.swp391grp3.bcourt.repo.UserRepo;

import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Service
public class CourtService {
    private final UserRepo userRepo;
    private final CourtRepo courtRepo;
    private final ModelMapper modelMapper;
    private final OrderService orderService;
    private final FileRepo fileRepo;
    private final FileService fileService;

    public Court createCourt(Court court){
        return courtRepo.save(court);
    }


    public Page<Court> getAllCourtStatusTrue(int page, int size, String sortBy, Sort.Direction sortOrder) {
        // Create Sort object based on the provided parameters
        Sort sort = Sort.by(sortOrder, sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        // Fetch sorted data from the repository
        return courtRepo.findAllByStatusTrueAndApprovalTrue(pageRequest);
    }
    public Page<Court> getAllCourt(int page, int size) {
        Page<Court> courtPage = courtRepo.findAll(PageRequest.of(page, size, Sort.by("courtName")));
        return courtPage;
    }

    public Page<Court> searchCourtsByName(int page, int size, String courtName){
        return courtRepo.findCourtByCourseName(courtName, PageRequest.of(page, size, Sort.by("courtName")));
    }
    public Court getCourtByCourtId(String courtId) {
        // Find the court by its ID
        Optional<Court> courtOpt = courtRepo.findByCourtId(courtId);

        // Check if the court is present, if not throw an exception
        if (courtOpt.isPresent()) {
            return courtOpt.get();
        } else {
            log.error("Court with ID {} not found", courtId);
            throw new RuntimeException("Court not found");
        }
    }

    public Page<CourtDTO> courtDTOConverter(int page, int size, Page<Court> courtPage){
        return courtPage.map(court -> modelMapper.map(court, CourtDTO.class));
    }

    public Page<Court> getCourtByUserId(int page, int size,String userId){
        return courtRepo.findByUser_UserId(userId, PageRequest.of(page, size, Sort.by("courtName")));
    }

    public CourtDTO courtReturnToDTO(Court court){
        return modelMapper.map(court, CourtDTO.class);
    }

    public void deleteCourt(Court court){

        courtRepo.delete(court);
    }

    public Court updateCourt(String courtId, Court updatedCourt) {
        Optional<Court> existingCourtOpt = courtRepo.findByCourtId(courtId);

        if (!existingCourtOpt.isPresent()) {
            log.error("Court with ID {} not found", courtId);
            throw new RuntimeException("Court not found");
        }

        Court existingCourt = existingCourtOpt.get();

        // Update fields if they are provided
        if (updatedCourt.getCourtName() != null) {
            existingCourt.setCourtName(updatedCourt.getCourtName());
        }
        if (updatedCourt.getAddress() != null) {
            existingCourt.setAddress(updatedCourt.getAddress());
        }
        if (updatedCourt.getDistrict() != null) {
            existingCourt.setDistrict(updatedCourt.getDistrict());
        }
        if (updatedCourt.getPrice() != null) {
            existingCourt.setPrice(updatedCourt.getPrice());
        }
        if (updatedCourt.getStatus() != null) {
            existingCourt.setStatus(updatedCourt.getStatus());
        }
        if (updatedCourt.getApproval() != null) {
            existingCourt.setApproval(updatedCourt.getApproval());
        }
        if (updatedCourt.getLicense() != null) {
            existingCourt.setLicense(updatedCourt.getLicense());
        }

        return courtRepo.save(existingCourt);
    }
    public void deleteCourtByCourtId(String courtId) {
        Optional<Court> courtToDeleteOpt = courtRepo.findByCourtId(courtId);

        if (courtToDeleteOpt.isPresent()) {
            Court courtToDelete = courtToDeleteOpt.get();


            // Get all orders associated with the court
            List<Order> ordersToDelete = orderService.getOrdersByCourtId(courtId);

            // Refund orders paid via E-Wallet
            ordersToDelete.stream()
                    .filter(order -> "E-Wallet".equals(order.getMethod().getMethodName()))
                    .forEach(order -> {
                        try {
                            orderService.refundForEWalletOrder(order.getOrderId());
                        } catch (IllegalArgumentException e) {
                            log.error("Error refunding order {} for court {}. Reason: {}", order.getOrderId(), courtId, e.getMessage());
                            // Handle exception if needed, e.g., log or propagate further
                        }
                    });
            List<User> users = userRepo.findByAssignedCourt_CourtId(courtId);
            for(User u : users){
                u.setAssignedCourt(null);
            }
            FileData oldFileData = courtToDelete.getFile();
            fileService.deleteOldFile(oldFileData);
            // Now delete the court itself
            orderService.deleteOrdersByCourtId(courtId);
            courtRepo.delete(courtToDelete);
        } else {
            throw new IllegalArgumentException("Court with ID " + courtId + " not found");
        }
    }
}
