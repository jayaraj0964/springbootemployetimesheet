package employeetimesheet.timesheet.dto; 

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TimeSheetDTO {
    private Integer timesheetId;
    private Integer categoryId;
    private Integer shiftId;
    private Integer userId;
    private LocalDate workDate;
    private LocalTime hoursWorked;
    private String details;
}
