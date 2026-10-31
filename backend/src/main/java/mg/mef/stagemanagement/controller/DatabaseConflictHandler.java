package mg.mef.stagemanagement.controller;

import java.sql.SQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class DatabaseConflictHandler {
    @ExceptionHandler({DataIntegrityViolationException.class, JpaSystemException.class})
    public ResponseEntity<ProblemDetail> databaseFailure(RuntimeException failure) {
        Throwable cause = failure;
        boolean conflict = failure instanceof DataIntegrityViolationException;
        while (cause != null) {
            if (cause instanceof SQLException sql && sql.getSQLState() != null)
                conflict |= sql.getSQLState().startsWith("23") || "P0001".equals(sql.getSQLState());
            cause = cause.getCause();
        }
        HttpStatus status = conflict ? HttpStatus.CONFLICT : HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, conflict
                ? "Cette opération ne respecte pas les contraintes du dossier. Actualisez les données et vérifiez les dates, transitions et doublons."
                : "Une erreur est survenue lors de l'enregistrement."));
    }
}
