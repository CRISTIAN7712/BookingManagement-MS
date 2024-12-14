package cd.codevs.BookingManagementService.controller;

import cd.codevs.BookingManagementService.entity.Booking;
import cd.codevs.BookingManagementService.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // Crear una nueva reserva
    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        Booking savedBooking = bookingService.createBooking(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
    }

    // Obtener una reserva por ID
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Listar todas las reservas
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    // Listar reservas por Tenant ID
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<Booking>> getBookingsByTenantId(@PathVariable String tenantId) {
        List<Booking> bookings = bookingService.getBookingsByTenantId(tenantId);
        return ResponseEntity.ok(bookings);
    }

    // Listar reservas por cliente
    @GetMapping("/tenant/{tenantId}/client/{email}")
    public ResponseEntity<List<Booking>> getBookingsForClient(@PathVariable String tenantId, @PathVariable String email) {
        List<Booking> bookings = bookingService.getBookingsForClient(tenantId, email);
        return ResponseEntity.ok(bookings);
    }

    // Listar reservas por recurso
    @GetMapping("/resource/{resourceId}")
    public ResponseEntity<List<Booking>> getBookingsByResourceId(@PathVariable Long resourceId) {
        List<Booking> bookings = bookingService.getBookingsByResourceId(resourceId);
        return ResponseEntity.ok(bookings);
    }

    // Listar reservas activas
    @GetMapping("/active")
    public ResponseEntity<List<Booking>> getActiveBookings() {
        List<Booking> bookings = bookingService.getActiveBookings();
        return ResponseEntity.ok(bookings);
    }

    // Cancelar una reserva
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Actualizar una reserva
    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody Booking booking) {
        try {
            Booking updatedBooking = bookingService.updateBooking(id, booking);
            return ResponseEntity.ok(updatedBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Eliminar una reserva
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

