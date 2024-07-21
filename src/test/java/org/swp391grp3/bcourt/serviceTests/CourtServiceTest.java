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
import org.swp391grp3.bcourt.dto.CourtDTO;
import org.swp391grp3.bcourt.entities.Court;
import org.swp391grp3.bcourt.entities.Order;
import org.swp391grp3.bcourt.entities.Paymentmethod;
import org.swp391grp3.bcourt.repo.CourtRepo;
import org.swp391grp3.bcourt.services.CourtService;
import org.swp391grp3.bcourt.services.OrderService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourtServiceTest {

    @Mock
    private CourtRepo courtRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private CourtService courtService;

    private Court court;
    private CourtDTO courtDTO;
    private Paymentmethod paymentMethod;
    private Order order;
    private Page<Court> courtPage;

    @BeforeEach
    public void setUp() {
        // Initialize Court
        court = new Court();
        court.setCourtId("1");
        court.setCourtName("Test Court");
        court.setStatus(true);
        court.setApproval(true);

        // Initialize CourtDTO
        courtDTO = new CourtDTO();
        courtDTO.setCourtId("1");
        courtDTO.setCourtName("Test Court");

        // Initialize Paymentmethod
        paymentMethod = new Paymentmethod();
        paymentMethod.setMethodId("1");
        paymentMethod.setMethodName("E-Wallet");

        // Initialize Order
        order = new Order();
        order.setOrderId("1");
        order.setUser(null); // Assume user is null or mock it as needed
        order.setDate(LocalDate.now());
        order.setBookingDate(LocalDate.now());
        order.setAmount(100.0);
        order.setSlotStart(LocalTime.of(9, 0));
        order.setSlotEnd(LocalTime.of(11, 0));
        order.setStatus(true);
        order.setRefund(false);
        order.setTransferStatus(false);
        order.setMethod(paymentMethod);
        order.setCourt(court);

        // Set up list of courts
        List<Court> courts = Arrays.asList(court);
        courtPage = new PageImpl<>(courts);
    }

    @Test
    public void testCreateCourt() {
        when(courtRepo.save(any(Court.class))).thenReturn(court);

        Court createdCourt = courtService.createCourt(court);

        assertNotNull(createdCourt);
        assertEquals("Test Court", createdCourt.getCourtName());
        verify(courtRepo, times(1)).save(court);
    }



    @Test
    public void testGetAllCourt() {
        when(courtRepo.findAll(any(PageRequest.class))).thenReturn(courtPage);

        Page<Court> result = courtService.getAllCourt(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(courtRepo, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    public void testSearchCourtsByName() {
        when(courtRepo.findCourtByCourseName(anyString(), any(PageRequest.class))).thenReturn(courtPage);

        Page<Court> result = courtService.searchCourtsByName(0, 10, "Test Court");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(courtRepo, times(1)).findCourtByCourseName(anyString(), any(PageRequest.class));
    }

    @Test
    public void testCourtDTOConverter() {
        when(modelMapper.map(any(Court.class), eq(CourtDTO.class))).thenReturn(courtDTO);

        Page<CourtDTO> result = courtService.courtDTOConverter(0, 10, courtPage);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Court", result.getContent().get(0).getCourtName());
        verify(modelMapper, times(1)).map(any(Court.class), eq(CourtDTO.class));
    }

    @Test
    public void testGetCourtByUserId() {
        when(courtRepo.findByUser_UserId(anyString(), any(PageRequest.class))).thenReturn(courtPage);

        Page<Court> result = courtService.getCourtByUserId(0, 10, "1");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(courtRepo, times(1)).findByUser_UserId(anyString(), any(PageRequest.class));
    }

    @Test
    public void testCourtReturnToDTO() {
        when(modelMapper.map(any(Court.class), eq(CourtDTO.class))).thenReturn(courtDTO);

        CourtDTO result = courtService.courtReturnToDTO(court);

        assertNotNull(result);
        assertEquals("Test Court", result.getCourtName());
        verify(modelMapper, times(1)).map(any(Court.class), eq(CourtDTO.class));
    }

    @Test
    public void testDeleteCourt() {
        doNothing().when(courtRepo).delete(any(Court.class));

        courtService.deleteCourt(court);

        verify(courtRepo, times(1)).delete(court);
    }

    @Test
    public void testUpdateCourt() {
        when(courtRepo.findByCourtId(anyString())).thenReturn(Optional.of(court));
        when(courtRepo.save(any(Court.class))).thenReturn(court);

        Court updatedCourt = new Court();
        updatedCourt.setCourtName("Updated Court");

        Court result = courtService.updateCourt("1", updatedCourt);

        assertNotNull(result);
        assertEquals("Updated Court", result.getCourtName());
        verify(courtRepo, times(1)).findByCourtId(anyString());
        verify(courtRepo, times(1)).save(any(Court.class));
    }

    @Test
    public void testDeleteCourtByCourtId() {
        when(courtRepo.findByCourtId(anyString())).thenReturn(Optional.of(court));
        List<Order> orders = Arrays.asList(order);
        when(orderService.getOrdersByCourtId(anyString())).thenReturn(orders);
        doNothing().when(orderService).deleteOrdersByCourtId(anyString());
        doNothing().when(courtRepo).delete(any(Court.class));
        doNothing().when(orderService).refundForEWalletOrder(anyString());

        courtService.deleteCourtByCourtId("1");

        verify(courtRepo, times(1)).findByCourtId(anyString());
        verify(orderService, times(1)).getOrdersByCourtId(anyString());
        verify(orderService, times(1)).deleteOrdersByCourtId(anyString());
        verify(courtRepo, times(1)).delete(any(Court.class));
        verify(orderService, times(1)).refundForEWalletOrder(anyString());
    }
}

