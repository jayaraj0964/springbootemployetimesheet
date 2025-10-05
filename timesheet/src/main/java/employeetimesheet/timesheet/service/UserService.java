package employeetimesheet.timesheet.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import employeetimesheet.timesheet.dto.UserDTO;
import employeetimesheet.timesheet.entity.AppUser;
import employeetimesheet.timesheet.entity.Role;
import employeetimesheet.timesheet.entity.Teams;
import employeetimesheet.timesheet.entity.User;
import employeetimesheet.timesheet.repository.RoleRepository;
import employeetimesheet.timesheet.repository.TeamRepository;
import employeetimesheet.timesheet.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TeamRepository teamRepository;

    public List<User> findAll() { 
        return userRepository.findAll(); 
    }

    public User findById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    private Role getRole(Integer roleId) {
        if (roleId == null || roleId == 0) {
            if (isAdmin()) {
                logger.info("⚠️ Role ID is null or 0 for admin, skipping role assignment");
                return null;
            }
            logger.warn("⚠️ Role ID is null or 0 for non-admin, assigning default role");
            return roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new IllegalArgumentException("Default role USER not found"));
        }
        return roleRepository.findById(roleId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + roleId));
    }

    private Teams getTeam(Long teamId) {
        if (teamId == null || teamId == 0) {
            if (isAdmin()) {
                logger.info("⚠️ Team ID is null or 0 for admin, skipping team assignment");
                return null;
            }
            logger.warn("⚠️ Team ID is null or 0 for non-admin, skipping team assignment");
            return null;
        }
        return teamRepository.findById(teamId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid team ID: " + teamId));
    }

    private void validateEnums(UserDTO dto) {
        if (dto.getGender() != null && !List.of("Male", "Female", "Other").contains(dto.getGender()))
            throw new IllegalArgumentException("Invalid gender");
        if (dto.getRelationship() != null && !List.of("Father", "Mother", "Sister", "Brother", "Spouse").contains(dto.getRelationship()))
            throw new IllegalArgumentException("Invalid relationship");
        if (dto.getEducationQualification() != null && !List.of("B.Tech", "Degree").contains(dto.getEducationQualification()))
            throw new IllegalArgumentException("Invalid education_qualification");
    }

    public User create(UserDTO dto, AppUser appUser) {
        logger.info("📝 Creating user profile for AppUser ID: {}", appUser.getId());
        validateEnums(dto);
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setMiddleName(dto.getMiddleName());
        user.setLastName(dto.getLastName());
        user.setBirthDate(dto.getBirthDate());
        user.setGender(dto.getGender());
        user.setSkills(dto.getSkills());
        user.setAddress(dto.getAddress());
        user.setContactNumber(dto.getContactNumber());
        user.setEmergencyContactName(dto.getEmergencyContactName());
        user.setEmergencyContactNumber(dto.getEmergencyContactNumber());
        user.setRelationship(dto.getRelationship());
        user.setEducationQualification(dto.getEducationQualification());
        user.setAppUser(appUser);

        // 🔗 Map team
        user.setTeam(getTeam(dto.getTeamid()));

        // 🔗 Map role
        user.setRole(getRole(dto.getRoleId()));

        User savedUser = userRepository.save(user);
        logger.info("✅ User profile created successfully for AppUser ID: {}", appUser.getId());
        return savedUser;
    }

    public User update(Integer id, UserDTO dto) {
        logger.info("📝 Updating user profile with ID: {}", id);
        validateEnums(dto);
        User existing = findById(id);
        if (dto.getRoleId() != null) existing.setRole(getRole(dto.getRoleId()));
        if (dto.getFirstName() != null) existing.setFirstName(dto.getFirstName());
        if (dto.getMiddleName() != null) existing.setMiddleName(dto.getMiddleName());
        if (dto.getLastName() != null) existing.setLastName(dto.getLastName());
        existing.setBirthDate(dto.getBirthDate());
        existing.setGender(dto.getGender());
        existing.setSkills(dto.getSkills());
        existing.setAddress(dto.getAddress());
        if (dto.getContactNumber() != null) existing.setContactNumber(dto.getContactNumber());
        existing.setEmergencyContactName(dto.getEmergencyContactName());
        existing.setEmergencyContactNumber(dto.getEmergencyContactNumber());
        existing.setRelationship(dto.getRelationship());
        existing.setEducationQualification(dto.getEducationQualification());
        User updatedUser = userRepository.save(existing);
        logger.info("✅ User profile updated successfully for ID: {}", id);
        return updatedUser;
    }

    public void delete(Integer id) { 
        logger.info("🗑️ Deleting user profile with ID: {}", id);
        userRepository.deleteById(id);
        logger.info("✅ User profile deleted successfully for ID: {}", id);
    }

    public List<User> searchByFirstName(String firstName) {
        logger.info("🔍 Searching users by firstName: {}", firstName);
        return userRepository.findByFirstName(firstName);
    }

    public List<User> filterByFirstName(String keyword) {
        logger.info("🔍 Filtering users by firstName containing: {}", keyword);
        return userRepository.findByFirstNameContainingIgnoreCase(keyword);
    }

    public List<User> searchByGender(String gender) {
        logger.info("🔍 Searching users by gender: {}", gender);
        return userRepository.findByGender(gender);
    }

    public List<User> filterByGender(String keyword) {
        logger.info("🔍 Filtering users by gender containing: {}", keyword);
        return userRepository.findByGenderContainingIgnoreCase(keyword);
    }
}