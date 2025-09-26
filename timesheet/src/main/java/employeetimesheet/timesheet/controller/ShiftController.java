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

import employeetimesheet.timesheet.dto.ShiftDTO;
import employeetimesheet.timesheet.entity.Shift;
import employeetimesheet.timesheet.service.ShiftService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftController {
    private final ShiftService shiftService;

    @GetMapping
    public List<Shift> getAll() { return shiftService.findAll(); }

    @GetMapping("/{id}")
    public Shift getById(@PathVariable Integer id) { return shiftService.findById(id); }

    @PostMapping("/postshifts")
    public ResponseEntity<Shift> create(@RequestBody ShiftDTO dto) {
        return ResponseEntity.ok(shiftService.create(dto));
    }

    @PutMapping("/{id}")
    public Shift update(@PathVariable Integer id, @RequestBody ShiftDTO dto) {
        return shiftService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        shiftService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

