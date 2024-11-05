package living.word.livingword.service;

import living.word.livingword.entity.PrayerRequest;
import living.word.livingword.entity.PrayerSupport;
import living.word.livingword.entity.User;
import living.word.livingword.exception.AlreadyPrayedException;
import living.word.livingword.exception.PrayerRequestNotFoundException;
import living.word.livingword.model.dto.PrayerRequestDTO;
import living.word.livingword.repository.PrayerRequestRepository;
import living.word.livingword.repository.PrayerSupportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PrayerRequestService {

    @Autowired
    private PrayerRequestRepository prayerRequestRepository;

    @Autowired
    private PrayerSupportRepository prayerSupportRepository;

    // Get User Authenticated
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("No authenticated user found");
    }

    // Crear nuevo pedido de oración
    public PrayerRequestDTO createPrayerRequest(String description) {
        User user = getCurrentUser();

        PrayerRequest prayerRequest = new PrayerRequest();
        prayerRequest.setDescription(description);
        prayerRequest.setDate(LocalDateTime.now());
        prayerRequest.setUser(user);

        PrayerRequest savedRequest = prayerRequestRepository.save(prayerRequest);

        // Mapear a DTO
        return new PrayerRequestDTO(
                savedRequest.getId(),
                savedRequest.getDescription(),
                savedRequest.getDate(),
                savedRequest.getPrayerCount(),
                user.getName()
        );
    }
    // Get prayers for date
    public Page<PrayerRequestDTO> getTodaysPrayerRequests(Pageable pageable) {
        LocalDate today = LocalDate.now();
        Page<PrayerRequest> prayerRequestsPage = prayerRequestRepository.findByDate(today, pageable);

        List<PrayerRequestDTO> prayerRequestsDto = prayerRequestsPage.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(prayerRequestsDto, pageable, prayerRequestsPage.getTotalElements());
    }

    // List of prayers
    public List<PrayerRequestDTO> getAllPrayerRequests() {
        List<PrayerRequest> prayerRequests = prayerRequestRepository.findAll();
        return prayerRequests.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    public Page<PrayerRequestDTO> getAllPrayerRequestsPaginated(Pageable pageable) {
        Page<PrayerRequest> prayerRequestsPage = prayerRequestRepository.findAll(pageable);
        List<PrayerRequestDTO> prayerRequestsDto = prayerRequestsPage.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(prayerRequestsDto, pageable, prayerRequestsPage.getTotalElements());
    }


    // Obtener los detalles de un pedido de oración específico
    public PrayerRequestDTO getPrayerRequestById(Long id) {
        PrayerRequest prayerRequest = prayerRequestRepository.findById(id)
                .orElseThrow(() -> new PrayerRequestNotFoundException("Prayer request with id " + id + " not found"));
        return convertToDto(prayerRequest);
    }

    // Obtener la lista de usuarios que oraron por un pedido específico
    public List<String> getSupportersForPrayer(Long requestId) {
        PrayerRequest prayerRequest = prayerRequestRepository.findById(requestId)
                .orElseThrow(() -> new PrayerRequestNotFoundException("Prayer request with id " + requestId + " not found"));

        List<PrayerSupport> supports = prayerSupportRepository.findByPrayerRequest(prayerRequest);
        return supports.stream().map(support -> support.getUser().getName()).collect(Collectors.toList());
    }

    @Transactional    // Delete a prayer request by your ID
    public void deletePrayerRequest(Long id) {
        PrayerRequest prayerRequest = prayerRequestRepository.findById(id)
                .orElseThrow(() -> new PrayerRequestNotFoundException("Prayer request with id " + id + " not found"));

        // Borrar todas las oraciones de apoyo relacionadas antes de borrar el pedido
        prayerSupportRepository.deleteByPrayerRequest(prayerRequest);
        prayerRequestRepository.delete(prayerRequest);
    }

    // Pray for a request
    public void supportPrayer(Long requestId) {
        User user = getCurrentUser();
        PrayerRequest prayerRequest = prayerRequestRepository.findById(requestId)
                .orElseThrow(() -> new PrayerRequestNotFoundException("Prayer request not found"));

        // Prevent the same user from praying multiple times
        if (prayerSupportRepository.existsByUserAndPrayerRequest(user, prayerRequest)) {
            throw new AlreadyPrayedException("User has already prayed for this request");
        }

        PrayerSupport support = new PrayerSupport();
        support.setUser(user);
        support.setPrayerRequest(prayerRequest);
        support.setSupportDate(LocalDateTime.now());

        prayerRequest.incrementPrayerCount();
        prayerSupportRepository.save(support);
        prayerRequestRepository.save(prayerRequest);
    }

    // Convert to Dto
    private PrayerRequestDTO convertToDto(PrayerRequest prayerRequest) {
        return new PrayerRequestDTO(
                prayerRequest.getId(),
                prayerRequest.getDescription(),
                prayerRequest.getDate(),
                prayerRequest.getPrayerCount(),
                prayerRequest.getUser().getName()
        );
    }
}