package cd.codevs.BookingManagementService.service;

import cd.codevs.BookingManagementService.entity.Booking;
import cd.codevs.BookingManagementService.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RestTemplate restTemplate;

    public Booking createBooking(Booking booking) {
        // Validar el tenant con TMS
        Boolean tenantExists = restTemplate.getForObject(
                "http://localhost:8081/api/tenants/" + booking.getTenantId() + "/exists",
                Boolean.class
        );
        if (tenantExists == null || !tenantExists) {
            throw new IllegalArgumentException("El tenant no es válido.");
        }

        // Validar la disponibilidad del recurso con RMS
        ResponseEntity<Boolean> availabilityResponse = restTemplate.getForEntity(
                "http://localhost:8082/api/resources/" + booking.getResourceId() +
                        "/availability?start=" + booking.getFechaInicio() + "&end=" + booking.getFechaFin(),
                Boolean.class
        );
        if (!Boolean.TRUE.equals(availabilityResponse.getBody())) {
            throw new IllegalArgumentException("El recurso no está disponible.");
        }

        booking.setEstado("ACTIVA");
        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsForClient(String tenantId, String clienteEmail) {
        return bookingRepository.findByTenantIdAndClienteEmail(tenantId, clienteEmail);
    }

    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new IllegalArgumentException("Reserva no encontrada.")
        );
        booking.setEstado("CANCELADA");
        bookingRepository.save(booking);
    }

    public boolean checkAvailability(String tenantId, Long resourceId, LocalDateTime start, LocalDateTime end) {
        List<Booking> bookings = bookingRepository.findByTenantIdAndResourceIdAndFechaInicioBetween(tenantId, resourceId, start, end);
        return bookings.isEmpty();
    }
}
