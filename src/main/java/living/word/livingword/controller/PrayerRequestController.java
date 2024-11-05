package living.word.livingword.controller;

import living.word.livingword.model.dto.PrayerRequestDTO;
import living.word.livingword.service.PrayerRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/prayers")
public class PrayerRequestController {

    @Autowired
    private PrayerRequestService prayerRequestService;

    // Crear pedido de oración
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PERM_PRAYER_READ') or hasAuthority('PERM_ADMIN_ACCESS')")
    public ResponseEntity<?> createPrayerRequest(@RequestBody String description) {
        PrayerRequestDTO prayerRequest = prayerRequestService.createPrayerRequest(description);
        return new ResponseEntity<>(prayerRequest, HttpStatus.CREATED);
    }

    // Orar por un pedido
    @PostMapping("/{id}/support")
    @PreAuthorize("hasAuthority('PERM_PRAYER_READ') or hasAuthority('PERM_ADMIN_ACCESS')")
    public ResponseEntity<?> supportPrayer(@PathVariable Long id) {
        prayerRequestService.supportPrayer(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Obtener todos los pedidos de oración
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('PERM_PRAYER_READ') or hasAuthority('PERM_ADMIN_ACCESS')")
    public ResponseEntity<List<PrayerRequestDTO>> getAllPrayerRequests() {
        List<PrayerRequestDTO> prayerRequests = prayerRequestService.getAllPrayerRequests();
        return new ResponseEntity<>(prayerRequests, HttpStatus.OK);
    }
    @GetMapping("/list-ordered")
    @PreAuthorize("hasAuthority('PERM_PRAYER_READ') or hasAuthority('PERM_ADMIN_ACCESS')")
    public ResponseEntity<Page<PrayerRequestDTO>> getAllPrayerRequestsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PrayerRequestDTO> prayerRequests = prayerRequestService.getAllPrayerRequestsPaginated(pageable);
        return new ResponseEntity<>(prayerRequests, HttpStatus.OK);
    }


    // Obtener detalles de un pedido específico
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_PRAYER_READ') or hasAuthority('PERM_ADMIN_ACCESS')")
    public ResponseEntity<PrayerRequestDTO> getPrayerRequestById(@PathVariable Long id) {
        PrayerRequestDTO prayerRequest = prayerRequestService.getPrayerRequestById(id);
        return new ResponseEntity<>(prayerRequest, HttpStatus.OK);
    }


    // Obtener la lista de personas que oraron por un pedido específico
    @GetMapping("/{id}/supporters")
    @PreAuthorize("hasAuthority('PERM_PRAYER_READ') or hasAuthority('PERM_ADMIN_ACCESS')")
    public ResponseEntity<List<String>> getSupportersForPrayer(@PathVariable Long id) {
        List<String> supporters = prayerRequestService.getSupportersForPrayer(id);
        return new ResponseEntity<>(supporters, HttpStatus.OK);
    }

    // Eliminar un pedido de oración
    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('PERM_PRAYER_DELETE') or hasAuthority('PERM_ADMIN_ACCESS')")
    public ResponseEntity<?> deletePrayerRequest(@PathVariable Long id) {
        prayerRequestService.deletePrayerRequest(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}