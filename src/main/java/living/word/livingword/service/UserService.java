package living.word.livingword.service;

import jakarta.transaction.Transactional;
import living.word.livingword.entity.Role;
import living.word.livingword.entity.User;
import living.word.livingword.exception.FileStorageException;
import living.word.livingword.model.dto.UpdateUserDto;
import living.word.livingword.model.dto.UserDTO;
import living.word.livingword.repository.AppUserRepository;
import living.word.livingword.repository.RoleRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private final Path profileImageRootLocation;
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("No se encontró un usuario autenticado");
    }
    @Autowired
    public UserService(@Value("${profile.images.path}") String profileImagesPath) {
        this.profileImageRootLocation = Paths.get(profileImagesPath);
        init();
    }

    private void init() {
        try {
            Files.createDirectories(profileImageRootLocation);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar el almacenamiento de imágenes", e);
        }
    }

    private String sanitizeFilename(String filename) {
        return Paths.get(filename).getFileName().toString();
    }

    private String saveProfileImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileStorageException("El archivo de imagen está vacío");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new FileStorageException("El archivo debe ser una imagen válida");
        }

        String originalFilename = sanitizeFilename(file.getOriginalFilename());
        String fileExtension = originalFilename.contains(".") ? 
                               originalFilename.substring(originalFilename.lastIndexOf(".")) : "";

        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        Path destinationFile = profileImageRootLocation.resolve(Paths.get(uniqueFilename)).normalize().toAbsolutePath();

        if (!destinationFile.getParent().equals(profileImageRootLocation.toAbsolutePath())) {
            throw new FileStorageException("No se puede almacenar el archivo fuera del directorio actual.");
        }

        try {
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException("No se pudo almacenar el archivo " + uniqueFilename, e);
        }

        return uniqueFilename;
    }
    @Transactional
    public User updateUserProfilePhoto(MultipartFile imageFile) {
        User currentUser = getCurrentUser();

        if (imageFile != null && !imageFile.isEmpty()) {
            // Eliminar la foto de perfil anterior si existe
            if (currentUser.getPhotoUrl() != null) {
                deleteImage(currentUser.getPhotoUrl());
            }

            // Guardar la nueva foto y actualizar la URL
            String photoUrl = saveProfileImage(imageFile);
            currentUser.setPhotoUrl(photoUrl);
        }

        // Guardar cambios en el usuario
        return userRepository.save(currentUser);
    }
    

    // Asignar rol a un usuario
    @Transactional
    public User assignRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));

        // Agregar permisos básicos de USER si el nuevo rol no es USER
        if(!role.getName().equalsIgnoreCase("USER")){
            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new IllegalArgumentException("Rol USER no encontrado"));
            role.getPermissions().addAll(userRole.getPermissions());
        }

        user.setRole(role);
        return userRepository.save(user);
    }

    // Remover rol de un usuario (volver a USER)
    @Transactional
    public User removeRoleFromUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalArgumentException("Rol USER no encontrado"));

        user.setRole(userRole);
        return userRepository.save(user);
    }
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());
    }
    // Buscar un usuario por ID
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return toUserDto(user);
    }
    @Transactional
    public UserDTO updateUser(Long userId, UpdateUserDto updateUserDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Actualizar los campos del usuario
        user.setName(updateUserDto.getName());
        user.setLastname(updateUserDto.getLastname());
        user.setPhone(updateUserDto.getPhone());
        user.setAddress(updateUserDto.getAddress());
        user.setGender(updateUserDto.getGender());
        user.setMaritalstatus(updateUserDto.getMaritalstatus());

        User updatedUser = userRepository.save(user);
        return toUserDto(updatedUser);
    }
    // Eliminar un usuario
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        userRepository.delete(user);
        }


    private UserDTO toUserDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setLastname(user.getLastname());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setGender(user.getGender());
        dto.setMaritalstatus(user.getMaritalstatus());
        dto.setRole(user.getRole().getName());
        if (user.getMinistry() != null) {
            dto.setMinistry(user.getMinistry().getName());
        }
        dto.setPhotoUrl(user.getPhotoUrl());
        return dto;
    }
    private void deleteImage(String filename) {
        try {
            Path imagePath = profileImageRootLocation.resolve(filename).normalize();
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            throw new FileStorageException("Error al eliminar la imagen: " + filename, e);
        }
    }
    public byte[] getProfileImage(String filename) {
        try {
            Path filePath = profileImageRootLocation.resolve(filename).normalize();
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new FileStorageException("No se pudo leer la imagen de perfil: " + filename, e);
        }
    }
    @Transactional
    public void deleteUserProfilePhoto() {
        User currentUser = getCurrentUser();

        if (currentUser.getPhotoUrl() != null) {
            deleteImage(currentUser.getPhotoUrl());
            currentUser.setPhotoUrl(null);
            userRepository.save(currentUser);
        }
    }

}