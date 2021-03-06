package rs.myst.backend.controllers;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.myst.backend.constants.RoleConstants;
import rs.myst.backend.repositories.PostRepository;
import rs.myst.backend.repositories.UserRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/jasper")
public class JasperReportsController {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public JasperReportsController(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    enum ReportType {
        POSTS,
        USERS
    }

    @GetMapping("/posts")
    @PreAuthorize(RoleConstants.ADMIN)
    public void getPostsReport(@RequestParam String from, @RequestParam String to, HttpServletResponse res) throws Exception {
        generateReport(ReportType.POSTS, from, to, "BlogPosts.jrxml", res);
    }

    @GetMapping("/users")
    @PreAuthorize(RoleConstants.ADMIN)
    public void getUsersReport(@RequestParam String from, @RequestParam String to, HttpServletResponse res) throws Exception {
        generateReport(ReportType.USERS, from, to, "Users.jrxml", res);
    }

    private void generateReport(ReportType type, String from, String to, String report, HttpServletResponse res) throws Exception {
        LocalDate dateFrom = LocalDate.parse(from);
        LocalDate dateTo = LocalDate.parse(to);

        Timestamp tsFrom = Timestamp.valueOf(dateFrom.atStartOfDay());
        Timestamp tsTo = Timestamp.valueOf(dateTo.atTime(23, 59));

        Collection<?> collection = switch (type) {
            case POSTS -> postRepository.findAllByCreatedAtBetween(tsFrom, tsTo, Sort.by(Sort.Direction.DESC, "createdAt"));
            case USERS -> userRepository.findAllByCreatedAtBetween(tsFrom, tsTo, Sort.by(Sort.Direction.DESC, "createdAt"));
        };

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(collection, false);

        try (InputStream is = this.getClass().getResourceAsStream("/jasper-reports/" + report)) {
            JasperReport jasperReport = JasperCompileManager.compileReport(is);

            Map<String, Object> params = new HashMap<>();
            params.put("from", dateFrom);
            params.put("to", dateTo);

            JasperPrint print = JasperFillManager.fillReport(jasperReport, params, dataSource);

            res.setContentType("application/pdf");

            JasperExportManager.exportReportToPdfStream(print, res.getOutputStream());
        }
    }
}
