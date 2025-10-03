package employeetimesheet.timesheet.controller;


import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import employeetimesheet.timesheet.dto.PositionDTO;
import employeetimesheet.timesheet.entity.Position;
import employeetimesheet.timesheet.service.PositionService;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {
    private final PositionService service;

        private static final Logger logger = LoggerFactory.getLogger(PositionController.class);


    @GetMapping
    public List<Position> getAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public Position getById(@PathVariable Integer id) { return service.findById(id); }

  @PostMapping("/postpositions")
public ResponseEntity<Position> create(@RequestBody PositionDTO dto) {
    logger.info("Received POST request for position: {}", dto);
    logger.debug("Attempting to create position with data: positionName={}, rolesResponsblities={}", 
                 dto.getPositionName(), dto.getRolesResponsblities());
    Position position = service.create(dto);
    return ResponseEntity.ok(position);
}

    @PutMapping("/{id}")
public Position update(@PathVariable Integer id, @RequestBody PositionDTO dto) {
    logger.info("Received PUT request for position ID: {}, data: {}", id, dto);
    return service.update(id, dto);
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

