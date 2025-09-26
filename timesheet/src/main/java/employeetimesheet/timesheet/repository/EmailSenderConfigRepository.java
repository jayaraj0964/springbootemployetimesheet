package employeetimesheet.timesheet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import employeetimesheet.timesheet.entity.EmailSenderConfig;

@Repository
public interface EmailSenderConfigRepository extends JpaRepository<EmailSenderConfig, Long> {
    Optional<EmailSenderConfig> findBySenderName(String senderName);
}
