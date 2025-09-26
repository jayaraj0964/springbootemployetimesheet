package employeetimesheet.timesheet.service;


import java.util.List;

import org.springframework.stereotype.Service;

import employeetimesheet.timesheet.dto.UserDTO;
import employeetimesheet.timesheet.entity.AppUser;
import employeetimesheet.timesheet.entity.Role;
import employeetimesheet.timesheet.entity.User;
import employeetimesheet.timesheet.repository.RoleRepository;
import employeetimesheet.timesheet.repository.UserRepository;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public List<User> findAll() { return userRepository.findAll(); }

    public User findById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    private Role getRole(Integer roleId) {
        return roleRepository.findById(roleId).orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));
    }

    private void validateEnums(UserDTO dto) {
        if (dto.getGender() != null && !List.of("Male","Female","Other").contains(dto.getGender()))
            throw new IllegalArgumentException("Invalid gender");
        if (dto.getRelationship() != null && !List.of("Father","Mother","Sister","Brother","Spouse").contains(dto.getRelationship()))
            throw new IllegalArgumentException("Invalid relationship");
        if (dto.getEducationQualification() != null && !List.of("B.Tech","Degree").contains(dto.getEducationQualification()))
            throw new IllegalArgumentException("Invalid education_qualification");
    }

   public User create(UserDTO dto, AppUser appUser) {
    validateEnums(dto);
    User user = User.builder()
            .role(getRole(dto.getRoleId()))
            .firstName(dto.getFirstName())
            .middleName(dto.getMiddleName())
            .lastName(dto.getLastName())
            .birthDate(dto.getBirthDate())
            .gender(dto.getGender())
            .skills(dto.getSkills())
            .address(dto.getAddress())
            .contactNumber(dto.getContactNumber())
            .emergencyContactName(dto.getEmergencyContactName())
            .emergencyContactNumber(dto.getEmergencyContactNumber())
            .relationship(dto.getRelationship())
            .educationQualification(dto.getEducationQualification())
            .appUser(appUser) // ✅ Ownership enforced
            .build();
    return userRepository.save(user);
}


    public User update(Integer id, UserDTO dto) {
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
        return userRepository.save(existing);
    }

    public void delete(Integer id) { userRepository.deleteById(id); }


//search filter

  public List<User> searchByFirstName(String firstName) {
    return userRepository.findByFirstName(firstName);
}

public List<User> filterByFirstName(String keyword) {
    return userRepository.findByFirstNameContainingIgnoreCase(keyword);
}

public List<User> searchByGender(String gender) {
    return userRepository.findByGender(gender);
}

public List<User> filterByGender(String keyword) {
    return userRepository.findByGenderContainingIgnoreCase(keyword);
}



}

