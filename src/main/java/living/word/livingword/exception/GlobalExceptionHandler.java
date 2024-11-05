package living.word.livingword.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Manejo de JWT inválido (firma incorrecta)
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorDetails> handleSignatureException(SignatureException ex) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Invalid JWT signature", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    // Manejo de JWT malformado
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorDetails> handleMalformedJwtException(MalformedJwtException ex) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Invalid JWT token", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    // Manejo de JWT expirado
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorDetails> handleExpiredJwtException(ExpiredJwtException ex) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "JWT token is expired", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    // Manejo de JWT no soportado
    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ErrorDetails> handleUnsupportedJwtException(UnsupportedJwtException ex) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "JWT token is unsupported", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    // Manejo de credenciales inválidas
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetails> handleBadCredentialsException(BadCredentialsException ex) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Invalid Credentials", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    // Manejo de usuario no encontrado
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "User Not Found", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    // Manejo de acceso denegado
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Access Denied", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    // Manejo de argumentos inválidos (validaciones)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse(ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Validation Failed", message);
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // Manejo de excepciones de almacenamiento de archivos
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorDetails> handleFileStorageException(FileStorageException ex) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "File Storage Error", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Manejo de Newsletter no encontrado
    @ExceptionHandler(NewsletterNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleNewsletterNotFoundException(NewsletterNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Newsletter Not Found", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    // Manejo de argumentos ilegales
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Bad Request", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // Manejo de excepciones de E/S
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorDetails> handleIOException(IOException ex) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "IO Error", "File handling error: " + ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Manejo de todas las demás excepciones
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleAllExceptions(Exception ex, WebRequest request) {
        logger.error("Unhandled exception: ", ex);
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Internal Server Error", "An error occurred: " + ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
