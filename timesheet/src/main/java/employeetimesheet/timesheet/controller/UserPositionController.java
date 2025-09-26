package employeetimesheet.timesheet.controller;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import employeetimesheet.timesheet.dto.UserPositionDTO;
import employeetimesheet.timesheet.entity.UserPosition;
import employeetimesheet.timesheet.service.UserPositionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user-positions")
@RequiredArgsConstructor
public class UserPositionController {
    private final UserPositionService service;

    @GetMapping
    public List<UserPosition> getAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public UserPosition getById(@PathVariable Integer id) { return service.findById(id); }

    @PostMapping("/postuser_positions")
    public ResponseEntity<UserPosition> create(@RequestBody UserPositionDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public UserPosition update(@PathVariable Integer id, @RequestBody UserPositionDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

