package employeetimesheet.timesheet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import employeetimesheet.timesheet.entity.TaskCategory;
@Repository
public interface TaskCategoryRepository extends JpaRepository <TaskCategory, Integer>{

}