package test.wallet.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import test.wallet.dto.ResponseDTO;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        ResponseDTO responseErrorDTO = new ResponseDTO();
        responseErrorDTO.setHttpCode((short) HttpStatus.CONFLICT.value());
        responseErrorDTO.setMessage(ex.getMessage());
        return handleExceptionInternal(ex, responseErrorDTO,
                new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = { RuntimeException.class })
    protected ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
        ResponseDTO responseErrorDTO = new ResponseDTO();
        responseErrorDTO.setHttpCode((short) HttpStatus.NOT_FOUND.value());
        responseErrorDTO.setMessage(ex.getMessage());
        return handleExceptionInternal(ex, responseErrorDTO,
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

}
