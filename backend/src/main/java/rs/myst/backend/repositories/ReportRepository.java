package rs.myst.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.myst.backend.model.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {
}