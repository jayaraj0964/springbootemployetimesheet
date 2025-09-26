package employeetimesheet.timesheet.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Data
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String token;

    @Temporal(TemporalType.TIMESTAMP) // ⏰ Ensures full timestamp is stored
    private Date expiryDate;
}
