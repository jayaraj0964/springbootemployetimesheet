package employeetimesheet.timesheet.dto;

import java.time.LocalDate;

import employeetimesheet.timesheet.entity.User;
import lombok.Data;

@Data
public class UserInfoDTO {
    private Long id;         // AppUser ID
    private String email;    // AppUser email
    private UserProfileDTO profile;

    // Constructor for basic info (no profile yet)
    public UserInfoDTO(Long id, String email) {
        this.id = id;
        this.email = email;
        this.profile = null;
    }

    // Constructor with full profile mapping
    public UserInfoDTO(Long id, String email, User user) {
        this.id = id;
        this.email = email;
        this.profile = user != null ? new UserProfileDTO(user) : null;
    }

    @Data
    public static class UserProfileDTO {
        private String firstName;
        private String middleName;
        private String lastName;
        private LocalDate birthDate;
        private String gender;
        private String skills;
        private String address;
        private String contactNumber;
        private String emergencyContactName;
        private String emergencyContactNumber;
        private String relationship;
        private String educationQualification;
        private Long teamId;
        private String teamName;
        private Long roleId;
        private String roleName;
        private Integer Userid;

        public UserProfileDTO(User user) {
            this.firstName = user.getFirstName();
            this.middleName = user.getMiddleName();
            this.lastName = user.getLastName();
            this.birthDate = user.getBirthDate();
            this.gender = user.getGender();
            this.skills = user.getSkills();
            this.address = user.getAddress();
            this.contactNumber = user.getContactNumber();
            this.emergencyContactName = user.getEmergencyContactName();
            this.emergencyContactNumber = user.getEmergencyContactNumber();
            this.relationship = user.getRelationship();
            this.educationQualification = user.getEducationQualification();
            this.Userid=user.getUserId();

            // 🔗 Team mapping
            this.teamId = user.getTeam() != null ? user.getTeam().getId() : null;
            this.teamName = user.getTeam() != null ? user.getTeam().getTeamname() : null;

            // 🔗 Role mapping
            this.roleId = user.getRole() != null ? Long.valueOf(user.getRole().getRoleId()) : null;
            this.roleName = user.getRole() != null ? user.getRole().getRoleName() : null;
        }
    }
}
