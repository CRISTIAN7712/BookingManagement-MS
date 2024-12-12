package cd.codevs.BookingManagementService.repository;

import cd.codevs.BookingManagementService.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByTenantIdAndResourceIdAndFechaInicioBetween(String tenantId, Long resourceId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByTenantIdAndClienteEmail(String tenantId, String clienteEmail);
}

