package employeetimesheet.timesheet.entity;

import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shift")
@Data 
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shift {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer shiftId;

    private LocalTime startTime;
    private LocalTime endTime;

    // DB ENUM('Morning','evening','late-night','us-holiday','Indian_holiday')
    // Use String to honor DB values with hyphens/underscores.
    @Column(name = "shift_name", length = 30)
    private String shiftName;

    @Column(name = "discription", length = 255)
    private String discription;


    @OneToMany(mappedBy = "shift", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Timesheet> timesheets;
}

