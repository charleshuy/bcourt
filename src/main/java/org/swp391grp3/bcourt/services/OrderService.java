package org.swp391grp3.bcourt.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.swp391grp3.bcourt.dto.OrderDTO;
import org.swp391grp3.bcourt.entities.Court;
import org.swp391grp3.bcourt.entities.Order;
import org.swp391grp3.bcourt.repo.CourtRepo;
import org.swp391grp3.bcourt.repo.OrderRepo;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepo orderRepo;
    private final CourtRepo courtRepo;
    private final ModelMapper modelMapper;
    public Order createOrder(Order order){
        String courtId = order.getCourt().getCourtId();
        Optional<Court> courtOpt = courtRepo.findById(courtId);
        if (!courtOpt.isPresent()) {
            throw new IllegalArgumentException("Court not found");
        }
        Court court = courtOpt.get();
        order.setCourt(court);
        order.setDate(LocalDate.now());
        if (order.getSlotStart() == null || order.getSlotEnd() == null) {
            throw new IllegalArgumentException("Slot start and end times must be provided");
        }
        if (order.getSlotStart().isAfter(order.getSlotEnd()) || order.getSlotStart().equals(order.getSlotEnd())) {
            throw new IllegalArgumentException("Slot start time must be before slot end time");
        }

        if (isSlotOverlapping(order)) {
            throw new IllegalArgumentException("Time slot overlaps with an existing order");
        }
        order.setAmount(amountCal(order));
        return orderRepo.save(order);
    }
    public Page<Order> getAllOrders(int page, int size) {
        return orderRepo.findAll(PageRequest.of(page, size));
    }
    public Page<Order> getAllOrdersByUserId(int page, int size, String userId) {
        Sort sort = Sort.by(Sort.Order.asc("date"), Sort.Order.asc("slotStart"));
        Page<Order> orders = orderRepo.findByUser_UserId(userId, PageRequest.of(page, size, sort));
        return orders;
    }
    public OrderDTO orderDTOConverter(Order order){
        return modelMapper.map(order, OrderDTO.class);
    }
    public Page<OrderDTO> orderDTOConverter(int page, int size, Page<Order> orders){
        return orders.map(order -> modelMapper.map(order, OrderDTO.class));
    }
    public Page<Order> getOrdersByCourtAndDate(String courtId, LocalDate date, int page, int size) {
        return orderRepo.findByCourtAndBookingDate(courtId, date, PageRequest.of(page, size));
    }
    private boolean isSlotOverlapping(Order order) {
        List<Order> existingOrders = orderRepo.findByCourtAndBookingDate(order.getCourt().getCourtId(), order.getBookingDate());
        for (Order existingOrder : existingOrders) {
            if (order.getSlotStart().isBefore(existingOrder.getSlotEnd()) && order.getSlotEnd().isAfter(existingOrder.getSlotStart())) {
                return true;
            }
        }
        return false;
    }
    public void deleteOrder(String orderId){
        Optional<Order> existingOrderOpt = orderRepo.findById(orderId);
        if (existingOrderOpt.isPresent()) {
            orderRepo.deleteById(orderId);
        } else {
            throw new IllegalArgumentException("Order not found");
        }
    }
    private Double amountCal(Order order) {
        double pricePerHour = order.getCourt().getPrice();
        long durationInHours = Duration.between(order.getSlotStart(), order.getSlotEnd()).toHours();
        return pricePerHour * durationInHours;
    }

}
