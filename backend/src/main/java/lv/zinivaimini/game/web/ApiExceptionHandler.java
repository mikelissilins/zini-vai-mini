package lv.zinivaimini.game.web;

import java.net.URI;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lv.zinivaimini.game.service.InvalidGameException;
import lv.zinivaimini.game.service.ResourceNotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail notFound(ResourceNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, "Resurss nav atrasts", exception.getMessage());
    }

    @ExceptionHandler({ InvalidGameException.class, MethodArgumentNotValidException.class })
    ProblemDetail invalid(Exception exception) {
        String detail = exception instanceof MethodArgumentNotValidException validation
                ? validation.getBindingResult().getAllErrors().stream().findFirst()
                        .map(error -> error.getDefaultMessage()).orElse("Ievadītie dati nav derīgi.")
                : exception.getMessage();
        return problem(HttpStatus.BAD_REQUEST, "Nederīgi spēles dati", detail);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    ProblemDetail conflict(OptimisticLockingFailureException exception) {
        return problem(HttpStatus.CONFLICT, "Versiju konflikts", exception.getMessage());
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("https://zini-vai-mini.app/problems/" + status.value()));
        return problem;
    }
}
