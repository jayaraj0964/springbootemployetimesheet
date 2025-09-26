package employeetimesheet.timesheet.dto; 


import lombok.Data;
import java.time.LocalTime;

@Data
public class ShiftDTO {
    private Integer shiftId;
    private LocalTime startTime;
    private LocalTime endTime;
    private String shiftName; // values must match DB ENUM
    private String discription;
}

