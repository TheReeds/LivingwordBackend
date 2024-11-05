package living.word.livingword.controller;

import jakarta.validation.Valid;
import living.word.livingword.entity.VideoCreateRequest;
import living.word.livingword.model.dto.VideoDto;
import living.word.livingword.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    // Add a new video link
    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('PERM_VIDEO_WRITE', 'PERM_ADMIN_ACCESS')")
    public ResponseEntity<?> addVideo(@Valid @RequestBody VideoCreateRequest videoRequest) {
        try {
            VideoDto savedVideo = videoService.addVideo(videoRequest);
            return new ResponseEntity<>(savedVideo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al añadir el video", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get video by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PERM_VIDEO_READ', 'PERM_ADMIN_ACCESS')")
    public ResponseEntity<VideoDto> getVideoById(@PathVariable Long id) {
        VideoDto video = videoService.getVideoById(id);
        return new ResponseEntity<>(video, HttpStatus.OK);
    }

    // Edit video
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PERM_VIDEO_EDIT', 'PERM_ADMIN_ACCESS')")
    public ResponseEntity<VideoDto> editVideo(@PathVariable Long id, @Valid @RequestBody VideoCreateRequest videoRequest) {
        VideoDto updatedVideo = videoService.editVideo(id, videoRequest);
        return new ResponseEntity<>(updatedVideo, HttpStatus.OK);
    }

    // Delete video
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PERM_VIDEO_DELETE', 'PERM_ADMIN_ACCESS')")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Get all videos with pagination and sorting
    @GetMapping
    @PreAuthorize("hasAnyAuthority('PERM_VIDEO_READ', 'PERM_ADMIN_ACCESS')")
    public ResponseEntity<List<VideoDto>> getAllVideos(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "desc") String sortOrder) { // Parámetro para el orden
    List<VideoDto> videos = videoService.getAllVideos(page, size, sortOrder);
    return new ResponseEntity<>(videos, HttpStatus.OK);
}
}