package employeetimesheet.timesheet.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import employeetimesheet.timesheet.dto.TimeSheetDTO;
import employeetimesheet.timesheet.entity.Shift;
import employeetimesheet.timesheet.entity.TaskCategory;
import employeetimesheet.timesheet.entity.Timesheet;
import employeetimesheet.timesheet.entity.User;
import employeetimesheet.timesheet.repository.ShiftRepository;
import employeetimesheet.timesheet.repository.TaskCategoryRepository;
import employeetimesheet.timesheet.repository.TimeSheetRepository;
import employeetimesheet.timesheet.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimesheetService {
    private final TimeSheetRepository timesheetRepository;
    private final TaskCategoryRepository taskCategoryRepository;
    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;

    public TaskCategory getCategory(Integer id) {
        if (id == null) return null;
        return taskCategoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
    }
    public Shift getShift(Integer id) {
        if (id == null) return null;
        return shiftRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Shift not found: " + id));
    }
    public User getUser(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    public List<Timesheet> findAll() { return timesheetRepository.findAll(); }

    public Timesheet findById(Integer id) {
        return timesheetRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Timesheet not found: " + id));
    }

    public Timesheet create(TimeSheetDTO dto) {
        Timesheet t = Timesheet.builder()
                .category(getCategory(dto.getCategoryId()))
                .shift(getShift(dto.getShiftId()))
                .user(getUser(dto.getUserId()))
                .workDate(dto.getWorkDate())
                .hoursWorked(dto.getHoursWorked())
                .details(dto.getDetails())
                .build();
        return timesheetRepository.save(t);
    }

    public Timesheet update(Integer id, TimeSheetDTO dto) {
        Timesheet t = findById(id);
        t.setCategory(getCategory(dto.getCategoryId()));
        t.setShift(getShift(dto.getShiftId()));
        if (dto.getUserId() != null) t.setUser(getUser(dto.getUserId()));
        t.setWorkDate(dto.getWorkDate());
        t.setHoursWorked(dto.getHoursWorked());
        t.setDetails(dto.getDetails());
        return timesheetRepository.save(t);
    }

    public void delete(Integer id) { timesheetRepository.deleteById(id); }
}

