package living.word.livingword.controller;

import living.word.livingword.entity.Attendance;
import living.word.livingword.model.dto.AttendanceFeedbackRequest;
import living.word.livingword.service.AttendanceService;
import living.word.livingword.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AppUserRepository userRepository;

    // Endpoint para que el usuario envíe su feedback de asistencia
    @PreAuthorize("hasAnyAuthority('PERM_ATTENDANCE_WRITE', 'PERM_ADMIN_ACCESS')")
    @PostMapping("/feedback")
    public ResponseEntity<?> submitAttendanceFeedback(@Valid @RequestBody AttendanceFeedbackRequest feedbackRequest) {
        try {
            // Llamar al servicio usando la solicitud de feedback
            attendanceService.recordAttendance(feedbackRequest.isAttended(),
                    feedbackRequest.getRating(), feedbackRequest.getFeedback());
            return ResponseEntity.ok("Feedback registrado correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    // Endpoint para que el usuario obtenga sus registros de asistencia
    @PreAuthorize("hasAnyAuthority('PERM_ATTENDANCE_READ', 'PERM_ADMIN_ACCESS')")
    @GetMapping
    public ResponseEntity<List<Attendance>> getUserAttendance() {
        // Llama al servicio sin necesidad de pasar el correo electrónico
        List<Attendance> attendanceRecords = attendanceService.getUserAttendance();
        return ResponseEntity.ok(attendanceRecords);
    }
}