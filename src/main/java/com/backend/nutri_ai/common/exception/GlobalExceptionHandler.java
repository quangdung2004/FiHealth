//package com.backend.nutri_ai.common.exception;
//
//import com.backend.nutri_ai.common.ApiResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.validation.ConstraintViolationException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.multipart.MaxUploadSizeExceededException;
//
//import java.util.stream.Collectors;
//
//@RestControllerAdvice
//@Slf4j
//public class GlobalExceptionHandler {
//
//    private String req(HttpServletRequest request) {
//        String method = request.getMethod();
//        String uri = request.getRequestURI();
//        String qs = request.getQueryString();
//        return method + " " + uri + (qs != null ? "?" + qs : "");
//    }
//
//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<ApiResponse<Void>> notFound(
//            ResourceNotFoundException ex,
//            HttpServletRequest request
//    ) {
//        log.warn("[NOT_FOUND] {} code={} msg={}", req(request), ex.getCode(), ex.getMessage());
//        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body(ApiResponse.fail(ex.getCode(), ex.getMessage()));
//    }
//
//    @ExceptionHandler(BadRequestException.class)
//    public ResponseEntity<ApiResponse<Void>> badRequest(
//            BadRequestException ex,
//            HttpServletRequest request
//    ) {
//        log.warn("[BAD_REQUEST] {} code={} msg={}", req(request), ex.getCode(), ex.getMessage());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(ApiResponse.fail(ex.getCode(), ex.getMessage()));
//    }
//
//    // @Valid body/parts
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiResponse<Void>> validation(
//            MethodArgumentNotValidException ex,
//            HttpServletRequest request
//    ) {
//        String msg = ex.getBindingResult().getFieldErrors().stream()
//                .map(err -> err.getField() + ": " + err.getDefaultMessage())
//                .collect(Collectors.joining("; "));
//
//        log.warn("[VALIDATION] {} msg={}", req(request), msg);
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(ApiResponse.fail("VALIDATION_ERROR", msg));
//    }
//
//    // validation on params/path variables
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<ApiResponse<Void>> constraintViolation(
//            ConstraintViolationException ex,
//            HttpServletRequest request
//    ) {
//        log.warn("[VALIDATION] {} msg={}", req(request), ex.getMessage());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(ApiResponse.fail("VALIDATION_ERROR", ex.getMessage()));
//    }
//
//    // JSON parse errors
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ResponseEntity<ApiResponse<Void>> notReadable(
//            HttpMessageNotReadableException ex,
//            HttpServletRequest request
//    ) {
//        log.warn("[INVALID_JSON] {} msg={}", req(request), ex.getMessage());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(ApiResponse.fail("INVALID_JSON", "Request body is invalid or unreadable"));
//    }
//
//    // file too large
//    @ExceptionHandler(MaxUploadSizeExceededException.class)
//    public ResponseEntity<ApiResponse<Void>> uploadTooLarge(
//            MaxUploadSizeExceededException ex,
//            HttpServletRequest request
//    ) {
//        log.warn("[UPLOAD_TOO_LARGE] {}", req(request));
//        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
//                .body(ApiResponse.fail("UPLOAD_TOO_LARGE", "Uploaded file exceeds the allowed size"));
//    }
//
//    // unique/foreign key violations
//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<ApiResponse<Void>> dataIntegrity(
//            DataIntegrityViolationException ex,
//            HttpServletRequest request
//    ) {
//        log.error("[DATA_INTEGRITY] {} msg={}", req(request), ex.getMostSpecificCause().getMessage());
//        return ResponseEntity.status(HttpStatus.CONFLICT)
//                .body(ApiResponse.fail("DATA_INTEGRITY_VIOLATION", "Data integrity violation"));
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<Void>> error(
//            Exception ex,
//            HttpServletRequest request
//    ) {
//        log.error("[INTERNAL_ERROR] {}", req(request), ex);
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(ApiResponse.fail("INTERNAL_ERROR", "Unexpected server error"));
//    }
//
//
//    @ExceptionHandler(StorageException.class)
//    public ResponseEntity<ApiResponse<Void>> storageError(
//            StorageException ex,
//            HttpServletRequest request
//    ) {
//        log.error("[STORAGE_ERROR] {} msg={}", req(request), ex.getMessage(), ex);
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(ApiResponse.fail(ex.getCode(), ex.getMessage()));
//    }
//}
