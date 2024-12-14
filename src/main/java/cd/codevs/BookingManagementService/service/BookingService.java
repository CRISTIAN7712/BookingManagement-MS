package cd.codevs.BookingManagementService.service;

import cd.codevs.BookingManagementService.entity.Booking;
import cd.codevs.BookingManagementService.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private  BookingRepository bookingRepository;

    @Autowired
    private  RestTemplate restTemplate;

    // Crear una nueva reserva
    public Booking createBooking(Booking booking) {
        Boolean tenantExists = restTemplate.getForObject(
                "http://localhost:8080/api/tenants/" + booking.getTenantId() + "/exists",
                Boolean.class
        );
        if (tenantExists == null || !tenantExists) {
            throw new IllegalArgumentException("El tenant no es válido.");
        }

        ResponseEntity<Boolean> availabilityResponse = restTemplate.getForEntity(
                "http://localhost:8080/api/resources/" + booking.getResourceId() +
                        "/availability?start=" + booking.getFechaInicio() + "&end=" + booking.getFechaFin(),
                Boolean.class
        );
        if (!Boolean.TRUE.equals(availabilityResponse.getBody())) {
            throw new IllegalArgumentException("El recurso no está disponible.");
        }

        booking.setEstado("ACTIVA");
        return bookingRepository.save(booking);
    }

    // Obtener una reserva por ID
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    // Listar todas las reservas
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // Listar reservas por Tenant ID
    public List<Booking> getBookingsByTenantId(String tenantId) {
        return bookingRepository.findByTenantId(tenantId);
    }

    // Listar reservas por cliente (email)
    public List<Booking> getBookingsForClient(String tenantId, String clienteEmail) {
        return bookingRepository.findByTenantIdAndClienteEmail(tenantId, clienteEmail);
    }

    // Listar reservas por recurso
    public List<Booking> getBookingsByResourceId(Long resourceId) {
        return bookingRepository.findByResourceId(resourceId);
    }

    // Listar reservas activas
    public List<Booking> getActiveBookings() {
        return bookingRepository.findByEstado("ACTIVA");
    }

    // Cancelar una reserva
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new IllegalArgumentException("Reserva no encontrada.")
        );
        booking.setEstado("CANCELADA");
        bookingRepository.save(booking);
    }

    // Verificar disponibilidad de recurso
    public boolean checkAvailability(String tenantId, Long resourceId, LocalDateTime start, LocalDateTime end) {
        List<Booking> bookings = bookingRepository.findByTenantIdAndResourceIdAndFechaInicioBetween(tenantId, resourceId, start, end);
        return bookings.isEmpty();
    }

    // Actualizar una reserva existente
    public Booking updateBooking(Long id, Booking updatedBooking) {
        Booking existingBooking = bookingRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Reserva no encontrada.")
        );
        existingBooking.setClienteNombre(updatedBooking.getClienteNombre());
        existingBooking.setClienteEmail(updatedBooking.getClienteEmail());
        existingBooking.setFechaInicio(updatedBooking.getFechaInicio());
        existingBooking.setFechaFin(updatedBooking.getFechaFin());
        existingBooking.setEstado(updatedBooking.getEstado());
        return bookingRepository.save(existingBooking);
    }

    // Eliminar una reserva
    public void deleteBooking(Long id) {
        if (bookingRepository.existsById(id)) {
            bookingRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Reserva no encontrada.");
        }
    }
}
