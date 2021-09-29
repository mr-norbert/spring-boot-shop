package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.AuditEventService;
import bnorbert.onlineshop.transfer.audit.AuditResponse;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.status;

@CrossOrigin
@RestController
@RequestMapping("/audit")
public class AuditController {

    private final AuditEventService auditEventService;

    public AuditController(AuditEventService auditEventService) {
        this.auditEventService = auditEventService;
    }

    @GetMapping
    public ResponseEntity<List<AuditEvent>> findAll(Pageable pageable) {
        Page<AuditEvent> page = auditEventService.findAll(pageable);
        return new ResponseEntity<>(page.getContent(), HttpStatus.OK);
    }

    @GetMapping("/findAllByLocalDateTimeBetween")
    public ResponseEntity<Page<AuditResponse>> findAllByAuditEventDateBetween(
            @RequestParam("dateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
            @RequestParam("toDateTime")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDateTime,
            Pageable pageable) {
        Page<AuditResponse> auditResponses = auditEventService.findAllByAuditEventDateBetween(dateTime, toDateTime, pageable);
        return new ResponseEntity<>(auditResponses, HttpStatus.OK);
    }

    @GetMapping("/findAllByLocalDateBetween")
    public ResponseEntity<Page<AuditResponse>> findAllByLocalDateBetween(
            @RequestParam("from") String from,
            LocalDate toDate,
            Pageable pageable) {
        Page<AuditResponse> auditResponses = auditEventService.findAllByLocalDateBetween(from, toDate, pageable);
        return new ResponseEntity<>(auditResponses, HttpStatus.OK);
    }


    @GetMapping("/findAllByPrincipal")
    public ResponseEntity<Page<AuditResponse>> findAllByEmail(String email, Pageable pageable) {
        Page<AuditResponse> auditResponses = auditEventService.findAllByEmail(email, pageable);
        return new ResponseEntity<>(auditResponses, HttpStatus.OK);
    }



    @GetMapping("/{id:.+}")
    public Optional<AuditEvent> findById(@PathVariable Long id) {
        return auditEventService.findById(id);
    }


    @GetMapping("/metadata")
    public ResponseEntity<AuditResponse> findMetadata(String name, String value) {
        List<AuditResponse> auditResponses = auditEventService.findMetadata(name, value);
        return new ResponseEntity(auditResponses, HttpStatus.OK);
    }

}
