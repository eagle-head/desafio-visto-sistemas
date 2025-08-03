package br.com.productmanagementsystem.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String PROBLEM_DETAILS_BASE_URL = "https://api.productmanagement.com.br";

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode statusCode,
                                                                  @NonNull WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        String detail = messageSource.getMessage("validation.error.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ex.getBody();
        problemDetail.setDetail(detail);
        problemDetail.setStatus(status.value());
        problemDetail.setType(URI.create(PROBLEM_DETAILS_BASE_URL + "/validation-error"));

        // Process field-level validation errors
        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    Map<String, String> error = new HashMap<>();
                    error.put("field", fieldError.getField());
                    error.put("message", messageSource.getMessage(fieldError, LocaleContextHolder.getLocale()));
                    return error;
                })
                .collect(Collectors.toList());

        // Process object-level (global) validation errors
        List<Map<String, String>> globalErrors = ex.getBindingResult().getGlobalErrors().stream()
                .map(objectError -> {
                    Map<String, String> error = new HashMap<>();
                    error.put("field", objectError.getObjectName());
                    error.put("message", messageSource.getMessage(objectError, LocaleContextHolder.getLocale()));
                    return error;
                })
                .toList();

        // Combine field and global errors
        fieldErrors.addAll(globalErrors);
        problemDetail.setProperty("errors", fieldErrors);

        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Object> handleProductNotFoundException(ProductNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String detail = messageSource.getMessage("product.not.found.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(URI.create(PROBLEM_DETAILS_BASE_URL + "/product-not-found"));
        problemDetail.setTitle(messageSource.getMessage("error.title.product.not.found", null, LocaleContextHolder.getLocale()));
        problemDetail.setProperty("productId", ex.getProductId());

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<Object> handleProductAlreadyExistsException(ProductAlreadyExistsException ex, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        String detail = messageSource.getMessage("product.already.exists.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(URI.create(PROBLEM_DETAILS_BASE_URL + "/product-already-exists"));
        problemDetail.setTitle(messageSource.getMessage("error.title.product.already.exists", null, LocaleContextHolder.getLocale()));
        problemDetail.setProperty("productName", ex.getProductName());

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String detail = ex.getMessage();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(URI.create(PROBLEM_DETAILS_BASE_URL + "/resource-not-found"));
        problemDetail.setTitle(messageSource.getMessage("error.title.resource.not.found", null, LocaleContextHolder.getLocale()));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String detail = messageSource.getMessage("illegal.argument.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(URI.create(PROBLEM_DETAILS_BASE_URL + "/invalid-argument"));
        problemDetail.setTitle(messageSource.getMessage("error.title.invalid.argument", null, LocaleContextHolder.getLocale()));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String detail = messageSource.getMessage("constraint.violation.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(URI.create(PROBLEM_DETAILS_BASE_URL + "/constraint-violation"));
        problemDetail.setTitle(messageSource.getMessage("error.title.constraint.violation", null, LocaleContextHolder.getLocale()));

        List<Map<String, String>> errors = ex.getConstraintViolations().stream()
                .map(violation -> {
                    Map<String, String> error = new HashMap<>();
                    error.put("field", extractFieldName(violation));
                    error.put("message", violation.getMessage());
                    if (violation.getInvalidValue() != null) {
                        error.put("invalidValue", violation.getInvalidValue().toString());
                    }
                    return error;
                })
                .collect(Collectors.toList());

        problemDetail.setProperty("errors", errors);
        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String detail = messageSource.getMessage("type.mismatch.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(URI.create(PROBLEM_DETAILS_BASE_URL + "/type-mismatch"));
        problemDetail.setTitle(messageSource.getMessage("error.title.type.mismatch", null, LocaleContextHolder.getLocale()));

        Map<String, String> error = new HashMap<>();
        error.put("field", ex.getName());
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "correct type";
        String message = messageSource.getMessage("type.mismatch.field.message",
                new Object[]{ex.getName(), expectedType}, LocaleContextHolder.getLocale());
        error.put("message", message);
        if (ex.getValue() != null) {
            error.put("invalidValue", ex.getValue().toString());
        }

        problemDetail.setProperty("errors", List.of(error));
        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        String detail = messageSource.getMessage("database.constraint.violation.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(URI.create(PROBLEM_DETAILS_BASE_URL + "/database-constraint-violation"));
        problemDetail.setTitle(messageSource.getMessage("error.title.database.constraint.violation", null, LocaleContextHolder.getLocale()));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode statusCode,
                                                                  @NonNull WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String detail = messageSource.getMessage("parsing.error.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = createProblemDetail(ex, status, detail, null, null, request);
        problemDetail.setType(URI.create(PROBLEM_DETAILS_BASE_URL + "/parsing-error"));
        problemDetail.setTitle(messageSource.getMessage("error.title.parsing.error", null, LocaleContextHolder.getLocale()));

        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            @NonNull MissingServletRequestParameterException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request) {

        String detail = messageSource.getMessage("missing.parameter.detail", null, LocaleContextHolder.getLocale());
        HttpStatus status = HttpStatus.valueOf(statusCode.value());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(URI.create(PROBLEM_DETAILS_BASE_URL + "/missing-parameter"));
        problemDetail.setTitle(messageSource.getMessage("error.title.missing.parameter", null, LocaleContextHolder.getLocale()));

        Map<String, String> error = new HashMap<>();
        error.put("field", ex.getParameterName());
        String message = messageSource.getMessage("missing.parameter.field.message",
                new Object[]{ex.getParameterName()}, LocaleContextHolder.getLocale());
        error.put("message", message);
        problemDetail.setProperty("errors", List.of(error));

        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        // Log complete error details for debugging (server-side only)
        logger.error("Unexpected error occurred", ex);
        
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String detail = messageSource.getMessage("internal.server.error.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(URI.create(PROBLEM_DETAILS_BASE_URL + "/internal-server-error"));
        problemDetail.setTitle(messageSource.getMessage("error.title.internal.server.error", null, LocaleContextHolder.getLocale()));

        // Never expose internal details to client
        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    private String extractFieldName(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        int lastDotIndex = path.lastIndexOf('.');
        return lastDotIndex > 0 ? path.substring(lastDotIndex + 1) : path;
    }
}