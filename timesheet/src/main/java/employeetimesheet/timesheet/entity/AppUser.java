package employeetimesheet.timesheet.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @Column(nullable = false, unique = true)
    private String username; 

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; 

    @Column(nullable = false, unique = true)
    private String email; 

    @OneToOne(mappedBy = "appUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;
}
