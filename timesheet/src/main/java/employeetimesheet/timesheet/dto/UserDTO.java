package employeetimesheet.timesheet.dto; 

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserDTO {
    private Integer userId;
    private Integer roleId;
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate birthDate;
    private String gender; // "Male" | "Female" | "Other"
    private String skills;
    private String address;
    private String contactNumber;
    private String emergencyContactName;
    private String emergencyContactNumber;
    private String relationship; // Father | Mother | Sister | Brother | Spouse
    private String educationQualification; // "B.Tech" | "Degree"
    private String email;
}
