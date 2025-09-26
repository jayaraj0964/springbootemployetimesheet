package employeetimesheet.timesheet.controller;
import lombok.RequiredArgsConstructor;

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
import employeetimesheet.timesheet.dto.TimeSheetDTO;
import employeetimesheet.timesheet.entity.Shift;
import employeetimesheet.timesheet.entity.TaskCategory;
import employeetimesheet.timesheet.entity.Timesheet;
import employeetimesheet.timesheet.entity.User;
import employeetimesheet.timesheet.repository.TaskCategoryRepository;
import employeetimesheet.timesheet.service.TimesheetService;


@RestController
@RequestMapping("/api/timesheets")
@RequiredArgsConstructor
public class TimesheetController {
    private final TimesheetService service;
    private final TaskCategoryRepository taskCategoryRepository;
    private final TimesheetService timesheetService;
    

    @GetMapping
    public List<Timesheet> getAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public Timesheet getById(@PathVariable Integer id) { return service.findById(id); }

    @PostMapping("/posttimesheet")
    public ResponseEntity<Timesheet> create(@RequestBody TimeSheetDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public Timesheet update(@PathVariable Integer id, @RequestBody TimeSheetDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

   
    @GetMapping("/category/{id}")
    public TaskCategory getTaskCategoryRepository(int id) {
       // return taskCategoryRepository.findBy(id).orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
        return service.getCategory(id);
    }

    @GetMapping("/shift/{id}")
    public Shift getShift(int id) {
        return service.getShift(id);
    }  
     
    @GetMapping("/user/{id}")
    public User getUser(int id) {
        return service.getUser(id);
    }
   
}

