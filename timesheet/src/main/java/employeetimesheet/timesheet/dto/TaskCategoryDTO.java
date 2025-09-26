package employeetimesheet.timesheet.dto; 


import lombok.Data;

@Data
public class TaskCategoryDTO {
    private Integer categoryId;
    private String categoryName;
    private String description;
}
