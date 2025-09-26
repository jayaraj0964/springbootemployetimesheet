package employeetimesheet.timesheet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import employeetimesheet.timesheet.entity.Shift;

@Repository
public interface ShiftRepository extends JpaRepository <Shift,Integer> {

    
} 