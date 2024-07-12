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
import org.swp391grp3.bcourt.dto.OrderDTO;
import org.swp391grp3.bcourt.entities.*;
import org.swp391grp3.bcourt.repo.CourtRepo;
import org.swp391grp3.bcourt.repo.OrderRepo;
import org.swp391grp3.bcourt.services.OrderService;
import org.swp391grp3.bcourt.services.UserService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private CourtRepo courtRepo;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private Court court;
    private Paymentmethod paymentMethod;
    private User user;
    private User courtOwner;
    private Page<Court> courtPage;
    private Role role;

    @BeforeEach
    public void setUp() {

        //Initialize Role
        role = new Role();
        role.setRoleId("1");
        role.setRoleName("User");

        //Initialize Court owner
        courtOwner = new User();
        courtOwner.setUserId("1");
        courtOwner.setName("Court Owner");
        courtOwner.setEmail("test@example.com");
        courtOwner.setPhone("1234567890");
        courtOwner.setWalletAmount(0.0);
        courtOwner.setRefundWallet(0.0);
        courtOwner.setBanCount(0);
        courtOwner.setEnabled(true);
        courtOwner.setRole(role);

        // Initialize User
        user = new User();
        user.setUserId("2");
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPhone("1234567890");
        user.setWalletAmount(400.0);
        user.setBanCount(0);
        user.setEnabled(true);
        user.setRole(role);

        // Initialize Court
        court = new Court();
        court.setCourtId("1");
        court.setCourtName("Test Court");
        court.setStatus(true);
        court.setApproval(true);
        court.setPrice(200.0);
        court.setUser(courtOwner);

        // Initialize Paymentmethod
        paymentMethod = new Paymentmethod();
        paymentMethod.setMethodId("1");
        paymentMethod.setMethodName("E-Wallet");

        // Initialize Order
        order = new Order();
        order.setOrderId("1");
        order.setBookingDate(LocalDate.now());
        order.setSlotStart(LocalTime.of(9, 0));
        order.setSlotEnd(LocalTime.of(11, 0));
        order.setMethod(paymentMethod);
        order.setAmount(400.0);
        order.setUser(user);
        order.setCourt(court);

        // Set up list of courts
        List<Court> courts = Arrays.asList(court);
        courtPage = new PageImpl<>(courts);
    }

    @Test
    public void testCreateOrder() {
        when(courtRepo.findById(anyString())).thenReturn(Optional.of(court));
        when(orderRepo.save(any(Order.class))).thenReturn(order);
        when(userService.getUserById(anyString())).thenReturn(user);

        Order result = orderService.createOrder(order);

        assertNotNull(result);
        assertEquals(order.getOrderId(), result.getOrderId());
        verify(orderRepo, times(1)).save(any(Order.class));
    }

    @Test
    public void testUpdateOrder() {
        when(orderRepo.findById(anyString())).thenReturn(Optional.of(order));
        when(orderRepo.save(any(Order.class))).thenReturn(order);

        Order updatedOrder = new Order();
        updatedOrder.setBookingDate(LocalDate.now().plusDays(2));
        updatedOrder.setSlotStart(LocalTime.of(14, 0));
        updatedOrder.setSlotEnd(LocalTime.of(16, 0));

        Order result = orderService.updateOrder(order.getOrderId(), updatedOrder);

        assertNotNull(result);
        assertEquals(updatedOrder.getBookingDate(), result.getBookingDate());
        assertEquals(updatedOrder.getSlotStart(), result.getSlotStart());
        assertEquals(updatedOrder.getSlotEnd(), result.getSlotEnd());
    }

    @Test
    public void testGetAllOrders() {
        Page<Order> orderPage = new PageImpl<>(Arrays.asList(order));
        when(orderRepo.findAll(any(PageRequest.class))).thenReturn(orderPage);

        Page<Order> result = orderService.getAllOrders(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(order.getOrderId(), result.getContent().get(0).getOrderId());
    }

    @Test
    public void testGetAllOrdersByUserId() {
        Page<Order> orderPage = new PageImpl<>(Arrays.asList(order));
        when(orderRepo.findByUser_UserId(anyString(), any(PageRequest.class))).thenReturn(orderPage);

        Page<Order> result = orderService.getAllOrdersByUserId(0, 10, user.getUserId());

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(order.getOrderId(), result.getContent().get(0).getOrderId());
    }

    @Test
    public void testOrderDTOConverter() {
        OrderDTO orderDTO = new OrderDTO();
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(orderDTO);

        OrderDTO result = orderService.orderDTOConverter(order);

        assertNotNull(result);
    }

    @Test
    public void testGetOrdersByCourtAndDate() {
        Page<Order> orderPage = new PageImpl<>(Arrays.asList(order));
        when(orderRepo.findByCourtAndBookingDate(anyString(), any(LocalDate.class), any(PageRequest.class))).thenReturn(orderPage);

        Page<Order> result = orderService.getOrdersByCourtAndDate(court.getCourtId(), LocalDate.now(), 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(order.getOrderId(), result.getContent().get(0).getOrderId());
    }

    @Test
    public void testDeleteOrderById() {
        when(orderRepo.findById(anyString())).thenReturn(Optional.of(order));

        orderService.deleteOrderById(order.getOrderId());

        verify(orderRepo, times(1)).deleteById(anyString());
    }

    @Test
    public void testGetOrdersByCourtId() {
        Page<Order> orderPage = new PageImpl<>(Arrays.asList(order));
        when(orderRepo.findByCourt_CourtId(anyString(), any(PageRequest.class))).thenReturn(orderPage);

        Page<Order> result = orderService.getOrdersByCourtId(court.getCourtId(), 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(order.getOrderId(), result.getContent().get(0).getOrderId());
    }

    @Test
    public void testGetOrdersByCourtIdList() {
        List<Order> orders = Arrays.asList(order);
        when(orderRepo.findByCourt_CourtId(anyString())).thenReturn(orders);

        List<Order> result = orderService.getOrdersByCourtId(court.getCourtId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(order.getOrderId(), result.get(0).getOrderId());
    }

    @Test
    public void testCancelOrder() {
        when(orderRepo.findById(anyString())).thenReturn(Optional.of(order));
        when(userService.getUserById(anyString())).thenReturn(user);

        orderService.cancelOrder(order.getOrderId(), user.getUserId());

        assertFalse(order.getStatus());
        verify(orderRepo, times(1)).save(any(Order.class));
    }

    @Test
    public void testRefundForEWalletOrder() {
        when(orderRepo.findById(anyString())).thenReturn(Optional.of(order));
        when(userService.getUserById(anyString())).thenReturn(user);

        orderService.refundForEWalletOrder(order.getOrderId());

        assertEquals(800.0, user.getWalletAmount());
        verify(userService, times(1)).updateUser(any(User.class));
        verify(orderRepo, times(1)).save(any(Order.class));
    }

    @Test
    public void testAutoCancelOrders() {
        List<Order> pendingOrders = Arrays.asList(order);
        when(orderRepo.findPendingOrdersPastBookingDate(any(LocalDate.class))).thenReturn(pendingOrders);
        when(orderRepo.save(any(Order.class))).thenReturn(order);

        orderService.autoCancelOrders();

        assertFalse(order.getStatus());
        verify(orderRepo, times(1)).save(any(Order.class));
    }

    @Test
    public void testTransferingWalletForCourtOwner() {
        List<Order> ordersMoreThanTenDaysOld = Arrays.asList(order);
        when(orderRepo.findOrdersMoreThanTenDaysOld(any(LocalDate.class))).thenReturn(ordersMoreThanTenDaysOld);

        orderService.transferingWalletForCourtOwner();

        assertTrue(order.getTransferStatus());
        verify(orderRepo, times(1)).save(any(Order.class));
        verify(userService, times(1)).updateUser(any(User.class));
    }
}
