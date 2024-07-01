package org.swp391grp3.bcourt.services;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.swp391grp3.bcourt.dto.OrderDTO;
import org.swp391grp3.bcourt.entities.Court;
import org.swp391grp3.bcourt.entities.Order;
import org.swp391grp3.bcourt.entities.User;
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
    private final UserService userService;
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
        if ("E-Wallet".equals(order.getMethod().getMethodName())) {
            User user = userService.getUserById(order.getUser().getUserId());
            if (user.getWalletAmount() < order.getAmount()) {
                throw new IllegalArgumentException("Insufficient wallet balance");
            }
            user.setWalletAmount(user.getWalletAmount() - order.getAmount());
            userService.updateUser(user);
        }
        return orderRepo.save(order);
    }
    public Order updateOrder(String orderId, Order updatedOrder) {
        Optional<Order> existingOrderOpt = orderRepo.findById(orderId);
        if (existingOrderOpt.isPresent()) {
            Order existingOrder = existingOrderOpt.get();

            // Update fields from updatedOrder if they are not null
            if (updatedOrder.getBookingDate() != null) {
                existingOrder.setBookingDate(updatedOrder.getBookingDate());
            }
            if (updatedOrder.getSlotStart() != null) {
                existingOrder.setSlotStart(updatedOrder.getSlotStart());
            }
            if (updatedOrder.getSlotEnd() != null) {
                existingOrder.setSlotEnd(updatedOrder.getSlotEnd());
            }
            if (updatedOrder.getMethod() != null) {
                existingOrder.setMethod(updatedOrder.getMethod());
            }
            if (updatedOrder.getStatus() != null) {
                existingOrder.setStatus(updatedOrder.getStatus());
            }
            if(updatedOrder.getStatus() == null){
                existingOrder.setStatus(null);
            }
            if (updatedOrder.getAmount() != null) {
                existingOrder.setAmount(updatedOrder.getAmount());
            }

            // Validate slot times and overlaps (if necessary)
            // Add your validation logic here if required

            return orderRepo.save(existingOrder);
        } else {
            throw new IllegalArgumentException("Order not found");
        }
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
            // Skip checking overlaps with orders that are cancelled (status = false)
            if (existingOrder.getStatus() != null && !existingOrder.getStatus()) {
                continue;
            }

            if (order.getSlotStart().isBefore(existingOrder.getSlotEnd()) && order.getSlotEnd().isAfter(existingOrder.getSlotStart())) {
                return true;
            }
        }
        return false;
    }
    public void deleteOrderById(String orderId){
        Optional<Order> existingOrderOpt = orderRepo.findById(orderId);
        if (existingOrderOpt.isPresent()) {
            orderRepo.deleteById(orderId);
        } else {
            throw new IllegalArgumentException("Order not found");
        }
    }
    public Page<Order> getOrdersByCourtId(String courtId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookingDate").descending());
        return orderRepo.findByCourt_CourtId(courtId, pageable);
    }
    private Double amountCal(Order order) {
        double pricePerHour = order.getCourt().getPrice();
        long durationInHours = Duration.between(order.getSlotStart(), order.getSlotEnd()).toHours();
        return pricePerHour * durationInHours;
    }

    @Scheduled(cron = "0 0 0 * * *") // Run at midnight every day
    public void autoCancelOrders() {
        log.info("Running auto-cancel orders task...");

        // Get current date
        LocalDate currentDate = LocalDate.now();

        // Retrieve pending orders (status null) past their booking date
        List<Order> pendingOrders = orderRepo.findPendingOrdersPastBookingDate(currentDate);

        // Update status to cancelled for each pending order found
        for (Order order : pendingOrders) {
            // Check if the order status is null or pending
            if (order.getStatus() == null) {
                order.setStatus(false); // Assuming false represents cancelled status

                // Handle user ban count increase if payment method is "Cash"
                if ("Cash".equals(order.getMethod().getMethodName())) {
                    User user = order.getUser();
                    long daysUntilBooking = Duration.between(LocalDate.now().atStartOfDay(), order.getBookingDate().atStartOfDay()).toDays();

                    if (daysUntilBooking < 3) {
                        user.setBanCount(user.getBanCount() + 1);
                        log.warn("Ban count increased for user {} due to auto-cancellation (Cash payment).", user.getUserId());

                        // Disable user if ban count exceeds threshold
                        userService.disableUserIfBanned(user);
                    }
                }

                orderRepo.save(order);
                log.info("Order {} auto-cancelled.", order.getOrderId());
            }
        }

        log.info("Auto-cancel orders task completed.");
    }

    public void cancelOrder(String orderId, String userId) {
        Optional<Order> orderOpt = orderRepo.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Order not found");
        }

        Order order = orderOpt.get();
        if (!order.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("User not authorized to cancel this order");
        }

        // Allow cancellation only if the status is null
        if (order.getStatus() != null) {
            throw new IllegalArgumentException("Order cannot be cancelled as it is not pending");
        }

        // Calculate the number of days until the booking date
        long daysUntilBooking = Duration.between(LocalDate.now().atStartOfDay(), order.getBookingDate().atStartOfDay()).toDays();

        User user = order.getUser();

        // Check ban count condition before allowing cancellation
        if (user.getBanCount() >= 4) {
            throw new IllegalArgumentException("User is not eligible to cancel the order due to exceeded ban count");
        }

        if ("E-Wallet".equals(order.getMethod().getMethodName())) {
            double refundPercentage;
            if (daysUntilBooking >= 2) {
                refundPercentage = 1.0; // 100% refund
            } else if (daysUntilBooking >= 0) {
                refundPercentage = 0.5; // 50% refund
            } else {
                throw new IllegalArgumentException("Cannot cancel order on or after the booking date");
            }

            // Refund logic
            double refundAmount = order.getAmount() * refundPercentage;
            user.setWalletAmount(user.getWalletAmount() + refundAmount);
            userService.updateUser(user);

            log.info("Order {} cancelled and {}% refunded.", orderId, (int) (refundPercentage * 100));

        } else if ("Cash".equals(order.getMethod().getMethodName())) {
            if (daysUntilBooking < 3) {
                user.setBanCount(user.getBanCount() + 1);
                log.warn("Ban count increased for user {} due to late cancellation.", userId);

                // Disable user if ban count exceeds threshold
                userService.disableUserIfBanned(user);
            }
            log.info("Order {} cancelled (Cash payment).", orderId);
        } else {
            throw new IllegalArgumentException("Unsupported payment method for cancellation.");
        }

        // Set order status to cancelled (assuming false represents cancelled)
        order.setStatus(false);
        orderRepo.save(order);
    }

}
