package employeetimesheet.timesheet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import employeetimesheet.timesheet.entity.AppUser;
import employeetimesheet.timesheet.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    
List<User> findByFirstName(String firstName);
List<User> findByFirstNameContainingIgnoreCase(String keyword);
List<User> findByGender(String gender);
List<User> findByGenderContainingIgnoreCase(String keyword);
Optional<User> findByAppUserId(Long appUserId);
boolean existsByAppUser(AppUser appUser); // ✅ For ownership check
    Optional<User> findByAppUser(AppUser appUser);
}
