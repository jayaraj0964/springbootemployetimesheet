package employeetimesheet.timesheet.dto; 

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSheetDTO {
    private Integer timesheetId;
    private Integer categoryId;
    private Integer shiftId;
    private Integer userId;
    private LocalDate workDate;
    private LocalTime hoursWorked;
    private String details;
}
 