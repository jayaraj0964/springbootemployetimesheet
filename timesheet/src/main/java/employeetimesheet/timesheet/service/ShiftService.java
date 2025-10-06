package employeetimesheet.timesheet.service;


import java.util.List;

import org.springframework.stereotype.Service;

import employeetimesheet.timesheet.dto.ShiftDTO;
import employeetimesheet.timesheet.entity.Shift;
import employeetimesheet.timesheet.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class ShiftService {
    private final ShiftRepository shiftRepository;

    private static final List<String> ALLOWED = List.of("Morning","evening","late-night","us-holiday","Indian_holiday","night_shift");

    private void validate(ShiftDTO dto) {
        if (dto.getShiftName() != null && !ALLOWED.contains(dto.getShiftName()))
            throw new IllegalArgumentException("Invalid shift_name");
    }

    public List<Shift> findAll() { return shiftRepository.findAll(); }

    public Shift findById(Integer id) {
        return shiftRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Shift not found: " + id));
    }

    public Shift create(ShiftDTO dto) {
        validate(dto);
        Shift s = Shift.builder()
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .shiftName(dto.getShiftName())
                .discription(dto.getDiscription())
                .build();
        return shiftRepository.save(s);
    }

    public Shift update(Integer id, ShiftDTO dto) {
        validate(dto);
        Shift s = findById(id);
        s.setStartTime(dto.getStartTime());
        s.setEndTime(dto.getEndTime());
        s.setShiftName(dto.getShiftName());
        s.setDiscription(dto.getDiscription());
        return shiftRepository.save(s);
    }

    public void delete(Integer id) { shiftRepository.deleteById(id); }
}

