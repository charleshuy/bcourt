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
import org.swp391grp3.bcourt.entities.Order;
import org.swp391grp3.bcourt.repo.CourtRepo;

import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Service
public class CourtService {
    private final CourtRepo courtRepo;
    private final ModelMapper modelMapper;
    private final OrderService orderService;

    public Court createCourt(Court court){
        return courtRepo.save(court);
    }


    public Page<Court> getAllCourtStatusTrue(int page, int size) {
        Page<Court> courtPage = courtRepo.findAllByStatusTrueAndApprovalTrue(PageRequest.of(page, size, Sort.by("courtName")));
        return courtPage;
    }
    public Page<Court> getAllCourt(int page, int size) {
        Page<Court> courtPage = courtRepo.findAll(PageRequest.of(page, size, Sort.by("courtName")));
        return courtPage;
    }

    public Page<Court> searchCourtsByName(int page, int size, String courtName){
        return courtRepo.findCourtByCourseName(courtName, PageRequest.of(page, size, Sort.by("courtName")));
    }

    public Page<CourtDTO> courtDTOConverter(int page, int size, Page<Court> courtPage){
        return courtPage.map(court -> modelMapper.map(court, CourtDTO.class));
    }

    public Page<Court> getCourtByUserId(int page, int size,String userId){
        return courtRepo.findByUser_UserId(userId, PageRequest.of(page, size));
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

            // Now delete the court itself
            orderService.deleteOrdersByCourtId(courtId);
            courtRepo.delete(courtToDelete);
        } else {
            throw new IllegalArgumentException("Court with ID " + courtId + " not found");
        }
    }
}
