package living.word.livingword.service;
import jakarta.transaction.Transactional;
import living.word.livingword.entity.User;
import living.word.livingword.entity.Video;
import living.word.livingword.entity.VideoCreateRequest;
import living.word.livingword.model.dto.VideoDto;
import living.word.livingword.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.threeten.bp.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;


@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    // Get the authenticated user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("No authenticated user found");
    }

    // Add a new video
    @Transactional
    public VideoDto addVideo(VideoCreateRequest videoRequest) {
        User currentUser = getCurrentUser();

        Video video = new Video();
        video.setTitle(videoRequest.getTitle());
        video.setYoutubeUrl(videoRequest.getYoutubeUrl());
        video.setUploadedBy(currentUser); // Relacionar el usuario con el video
        video.setUploadedDate(LocalDateTime.now());

        Video savedVideo = videoRepository.save(video);

        return convertToDto(savedVideo);
    }
    // Convert Video entity to VideoDto
    public VideoDto convertToDto(Video video) {
        VideoDto dto = new VideoDto();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setYoutubeUrl(video.getYoutubeUrl());
        dto.setUploadedDate(video.getUploadedDate());

        // Asignar el username si el usuario no es nulo
        if (video.getUploadedBy() != null) {
            dto.setUploadedByUsername(video.getUploadedBy().getUsername());
        } else {
            dto.setUploadedByUsername("Unknown");
        }

        return dto;
    }

    // Get all videos with pagination and sorting
    public List<VideoDto> getAllVideos(int page, int size, String sortOrder) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "uploadedDate"));
        Page<Video> videosPage = videoRepository.findAll(pageable);
        return videosPage.stream()
                .map(this::convertToDto)
                .toList();
    }
    
    @Transactional
    public VideoDto editVideo(Long id, VideoCreateRequest videoRequest) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video no encontrado"));
        video.setTitle(videoRequest.getTitle());
        video.setYoutubeUrl(videoRequest.getYoutubeUrl());
        video.setPublicationDate(videoRequest.getPublicationDate());
        
        Video updatedVideo = videoRepository.save(video);
        return convertToDto(updatedVideo);
    }

    // Delete video
    @Transactional
    public void deleteVideo(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video no encontrado"));
        videoRepository.delete(video);
    }
    // Get video by ID
    public VideoDto getVideoById(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video no encontrado"));
        return convertToDto(video);
    }
    
}