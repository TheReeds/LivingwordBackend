package living.word.livingword.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Agrega este import para HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; // Agrega este import para RequestBody
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import living.word.livingword.entity.User;
import living.word.livingword.model.dto.DeviceTokenRequest;
import living.word.livingword.service.DeviceTokenService;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/device-tokens")
@Slf4j
public class DeviceTokenController {
    private final DeviceTokenService deviceTokenService;

    @Autowired
    public DeviceTokenController(DeviceTokenService deviceTokenService) {
        this.deviceTokenService = deviceTokenService;
    }

    @PostMapping
    public ResponseEntity<?> registerToken(@Valid @RequestBody DeviceTokenRequest request,
                                         @AuthenticationPrincipal User currentUser) {
        try {
            deviceTokenService.addDeviceToken(currentUser, request.getToken());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error registering device token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error registering device token");
        }
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> removeToken(@RequestParam String token,
                                       @AuthenticationPrincipal User currentUser) {
        try {
            deviceTokenService.removeDeviceToken(token);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error removing device token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error removing device token");
        }
    }
}