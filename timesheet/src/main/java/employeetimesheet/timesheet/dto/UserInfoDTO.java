package employeetimesheet.timesheet.dto;

import java.time.LocalDate;

import employeetimesheet.timesheet.entity.User;
import lombok.Data;

@Data
    public class UserInfoDTO {
      private Long id;
        private String email;
        private UserProfileDTO profile; // Nested DTO for profile data

        public UserInfoDTO(Long id, String email) {
            this.id = id;
            this.email = email;
            this.profile = null;
        }

        public UserInfoDTO(Long id, String email, User profile) {
            this.id = id;
            this.email = email;
            this.profile = profile != null ? new UserProfileDTO(profile) : null;
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
        }
    }
}
    

