package cd.codevs.BookingManagementService.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tenantId;
    private Long resourceId;
    private String clienteNombre;
    private String clienteEmail;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String estado;

    // Getters y Setters
}
