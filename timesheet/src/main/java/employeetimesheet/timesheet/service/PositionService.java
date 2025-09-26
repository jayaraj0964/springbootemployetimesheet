package employeetimesheet.timesheet.service;

import java.util.List;

import org.springframework.stereotype.Service;

import employeetimesheet.timesheet.dto.PositionDTO;
import employeetimesheet.timesheet.entity.Position;
import employeetimesheet.timesheet.repository.PositionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final PositionRepository repository;

    public List<Position> findAll() { 
        return repository.findAll();
     }

    public Position findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Position not found: " + id));
    }

    public Position create(PositionDTO dto) {
        Position p = Position.builder()
                .positionName(dto.getPositionName())
                .rolesResponsblities(dto.getRolesResponsblities())
                .build();
        return repository.save(p);
    }

    public Position update(Integer id, PositionDTO dto) {
        Position p = findById(id);
        p.setPositionName(dto.getPositionName());
        p.setRolesResponsblities(dto.getRolesResponsblities());
        return repository.save(p);
    }

    public void delete(Integer id) { repository.deleteById(id); }
}

