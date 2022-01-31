package rs.myst.backend.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Report {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ReportType type;

    @Basic
    @Column(name = "date")
    private Timestamp date;

    @Basic
    @Column(name = "reason")
    private String reason;

    @Basic
    @Column(name = "subject_id")
    private String subjectId;

    @ManyToOne
    @JoinColumn(name = "user_username", referencedColumnName = "username", nullable = false)
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return id == report.id && Objects.equals(subjectId, report.subjectId) && Objects.equals(type, report.type) && Objects.equals(date, report.date) && Objects.equals(reason, report.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, date, reason, subjectId);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
