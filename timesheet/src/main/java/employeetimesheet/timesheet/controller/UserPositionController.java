package employeetimesheet.timesheet.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user-positions")
@RequiredArgsConstructor
public class UserPositionController {
    private static final Logger logger = LoggerFactory.getLogger(UserPositionController.class);
    private final UserPositionService service;

    @GetMapping
    public List<UserPositionDTO> getAll() {
        logger.info("Fetching all user positions");
        return service.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserPositionDTO getById(@PathVariable Integer id) {
        logger.info("Fetching user position with ID: {}", id);
        return toDTO(service.findById(id));
    }

    @PostMapping("/postuser_positions")
    public ResponseEntity<UserPositionDTO> create(@Valid @RequestBody UserPositionDTO dto) {
        logger.info("Creating user position: {}", dto);
        UserPosition up = service.create(dto);
        return ResponseEntity.ok(toDTO(up));
    }

    @PutMapping("/{id}")
    public UserPositionDTO update(@PathVariable Integer id, @Valid @RequestBody UserPositionDTO dto) {
        logger.info("Updating user position ID: {} with data: {}", id, dto);
        return toDTO(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        logger.info("Deleting user position with ID: {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private UserPositionDTO toDTO(UserPosition up) {
        UserPositionDTO dto = new UserPositionDTO();
        dto.setUserPositionId(up.getUserPositionId());
        dto.setUserId(up.getUser().getUserId());
        dto.setPositionId(up.getPosition().getPositionId());
        dto.setDescription(up.getDescription());
        // Add user and position details for frontend
        dto.setUserFirstName(up.getUser().getFirstName());
        dto.setPositionName(up.getPosition().getPositionName());
        return dto;
    }
}