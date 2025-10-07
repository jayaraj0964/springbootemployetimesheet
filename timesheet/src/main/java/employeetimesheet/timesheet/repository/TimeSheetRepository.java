package employeetimesheet.timesheet.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import employeetimesheet.timesheet.entity.Timesheet;

@Repository
public interface TimeSheetRepository extends JpaRepository<Timesheet, Integer> {
    List<Timesheet> findByUserUserId(Integer userId);
    List<Timesheet> findByWorkDate(LocalDate workDate);
}