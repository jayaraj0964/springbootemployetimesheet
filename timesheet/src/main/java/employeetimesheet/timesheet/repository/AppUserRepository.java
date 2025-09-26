package employeetimesheet.timesheet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import employeetimesheet.timesheet.entity.AppUser;
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByEmail(String email); // 👈 Correct placement

    // Optional: default method with logging (if needed)
    default Optional<AppUser> logAndFindByUsername(String username) {
        System.out.println("🔎 Checking if username exists in DB: " + username);
        return findByUsername(username);
    }
}

