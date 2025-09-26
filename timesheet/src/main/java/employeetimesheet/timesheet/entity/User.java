package employeetimesheet.timesheet.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId; // 🔑 Primary key for profile

    // 🔗 Link to AppUser (login owner)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id", referencedColumnName = "id", unique = true)
    @JsonIgnore
    private AppUser appUser;

    // 🔗 Team relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", insertable = true, updatable = true)
    @JsonIgnore
    private Teams team;

    // 🔗 Role relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = true, updatable = true)
    @JsonIgnore
    private Role role;

    @NotBlank
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank
    @Column(name = "middle_name", nullable = false, length = 100)
    private String middleName;

    @NotBlank
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    private LocalDate birthDate;

    @Column(columnDefinition = "ENUM('Male','Female','Other')")
    private String gender;

    @Column(length = 1000)
    private String skills;

    @Column(columnDefinition = "TEXT")
    private String address;

    @NotBlank
    @Column(name = "contact_number", nullable = false, length = 15)
    private String contactNumber;

    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Column(name = "emergency_contact_number", length = 15)
    private String emergencyContactNumber;

    @Column(columnDefinition = "ENUM('Father','Mother','Sister','Brother','Spouse')")
    private String relationship;

    @Column(name = "education_qualification", columnDefinition = "ENUM('B.Tech','Degree')")
    private String educationQualification;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Timesheet> timesheets;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<UserPosition> userPositions;
}
