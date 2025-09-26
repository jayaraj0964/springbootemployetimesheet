package employeetimesheet.timesheet.service;


import java.util.List;

import org.springframework.stereotype.Service;

import employeetimesheet.timesheet.dto.Roledto;
import employeetimesheet.timesheet.entity.Role;
import employeetimesheet.timesheet.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public List<Role> findAll() { return roleRepository.findAll(); }

    public Role findById(Integer id) {
        return roleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Role not found: " + id));
    }

    public Role create(Roledto dto) {
        Role role = Role.builder()
                .roleName(dto.getRoleName())
                .description(dto.getDescription())
                .build();
        return roleRepository.save(role);
    }

    public Role update(Integer id, Roledto dto) {
        Role existing = findById(id);
        existing.setRoleName(dto.getRoleName());
        existing.setDescription(dto.getDescription());
        return roleRepository.save(existing);
    }

    public void delete(Integer id) { roleRepository.deleteById(id); }
}

