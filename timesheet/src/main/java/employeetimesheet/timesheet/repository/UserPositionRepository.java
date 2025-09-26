package employeetimesheet.timesheet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import employeetimesheet.timesheet.entity.UserPosition;

@Repository
public interface UserPositionRepository extends JpaRepository<UserPosition,Integer> {    
}