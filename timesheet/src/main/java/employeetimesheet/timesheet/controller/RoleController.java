package employeetimesheet.timesheet.controller;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import employeetimesheet.timesheet.dto.Roledto;
import employeetimesheet.timesheet.entity.Role;
import org.springframework.web.bind.annotation.RequestBody;
import employeetimesheet.timesheet.service.RoleService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

     private static final Logger log = LoggerFactory.getLogger(RoleController.class);

    @GetMapping("/getallroles")
    public List<Role> getAll() { return roleService.findAll(); }

    @GetMapping("/{id}")
    public Role getById(@PathVariable Integer id) { return roleService.findById(id); }

    @PostMapping("/postrole")
    public ResponseEntity<Role> create(@RequestBody Roledto dto) {
    log.info("Received Role DTO: {}", dto); //  Log the incoming DTO
    Role role = roleService.create(dto);    // Create the Role
    log.info("Created Role Entity: {}", role); // Log the created Role
    return ResponseEntity.ok(role);
}

    @PutMapping("/{id}")
    public Role update(@PathVariable Integer id, @RequestBody Roledto dto) {
        return roleService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

