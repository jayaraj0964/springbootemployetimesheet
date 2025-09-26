package employeetimesheet.timesheet.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "email_sender_config")
@Data
public class EmailSenderConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String senderName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String appPassword;
}
