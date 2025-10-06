package employeetimesheet.timesheet.dto; 

import lombok.Data;

@Data
public class UserPositionDTO {
    private Integer userPositionId;
    private Integer userId;
    private Integer positionId;
    private String description;
    private String userFirstName;
    private String positionName;    
}
