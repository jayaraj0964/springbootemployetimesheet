package employeetimesheet.timesheet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import employeetimesheet.timesheet.entity.Teams;


@Repository
public interface TeamRepository extends JpaRepository<Teams,Long>{
        List <Teams> findByTeamname(String teamname); //it extract team name ex: orders
        // List<Teams> findByTeameKeyword(String keyword); // serech with partial match like %keyword%
        List<Teams> findByTeamnameContainingIgnoreCase(String keyword); // ✅ Correct

}
