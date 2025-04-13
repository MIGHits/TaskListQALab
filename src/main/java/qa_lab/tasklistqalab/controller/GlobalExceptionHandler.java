package qa_lab.tasklistqalab.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import qa_lab.tasklistqalab.dto.ResponseModel;
import qa_lab.tasklistqalab.exception.BadRequest;
import qa_lab.tasklistqalab.exception.NotFound;

import java.util.Arrays;
import java.util.stream.Collectors;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFound.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseModel.class)))
    })
    public ResponseEntity<ResponseModel> handleNotFound(NotFound ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BadRequest.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseModel.class)))
    })
    public ResponseEntity<ResponseModel> handleBadRequest(BadRequest ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseModel.class)))
    })
    public ResponseEntity<ResponseModel> handleValidation(MethodArgumentNotValidException ex) {
        String errorDetails = ex.getBindingResult().getAllErrors()
                .stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return "Поле '" + fieldError.getField() + "': " + fieldError.getDefaultMessage();
                    } else {
                        return error.getDefaultMessage();
                    }
                })
                .collect(Collectors.joining("; "));

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed: " + errorDetails
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Invalid message format",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseModel.class)))
    })
    public ResponseEntity<ResponseModel> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String errorMessage = "Некорректный формат данных";

        if (ex.getCause() instanceof InvalidFormatException cause) {
            String fieldName = cause.getPath().getFirst().getFieldName();
            String invalidValue = cause.getValue().toString();

            if (cause.getTargetType().isEnum()) {
                String enumValues = Arrays.stream(cause.getTargetType().getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
                errorMessage = String.format(
                        "Поле '%s': значение '%s' неверно. Допустимые значения: [%s]",
                        fieldName, invalidValue, enumValues
                );
            }
        }

        return buildResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(Exception.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseModel.class)))
    })
    public ResponseEntity<ResponseModel> handleAllExceptions(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error: " + ex.getMessage());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Missing request header",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseModel.class)))
    })
    public ResponseEntity<ResponseModel> handleMissingHeader(MissingRequestHeaderException ex) {
        String message = "Отсутствует заголовок: " + ex.getHeaderName();
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseEntity<ResponseModel> buildResponse(HttpStatus status, String message) {
        return new ResponseEntity<>(ResponseModel.builder()
                .status(status.getReasonPhrase())
                .message(message)
                .build(), status);
    }
}
