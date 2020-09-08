package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.AuditEventService;

import bnorbert.onlineshop.transfer.audit.AuditResponse;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/audits")
public class AuditController {

    private final AuditEventService auditEventService;

    public AuditController(AuditEventService auditEventService) {
        this.auditEventService = auditEventService;
    }

    @GetMapping
    public ResponseEntity<List<AuditEvent>> getAll(Pageable pageable) {
        Page<AuditEvent> page = auditEventService.findAll(pageable);
        return new ResponseEntity<>(page.getContent(), HttpStatus.OK);
    }

    @GetMapping("/findByPrincipal")
    public ResponseEntity<Page<AuditResponse>> findByPrincipal(
            String email, Pageable pageable) {
        Page<AuditResponse> auditResponses = auditEventService.findAllByPrincipal(email, pageable);
        return new ResponseEntity<>(auditResponses, HttpStatus.OK);
    }

    @GetMapping("/{id:.+}")
    public Optional<AuditEvent> get(@PathVariable Long id) {
        return auditEventService.find(id);
    }
}
