package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.AuditEventService;
import bnorbert.onlineshop.transfer.audit.AuditResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/audit")
public class AuditController {

    private final AuditEventService auditEventService;

    public AuditController(AuditEventService auditEventService) {
        this.auditEventService = auditEventService;
    }

    @GetMapping("/metadata")
    public ResponseEntity<AuditResponse> findMetadata(String name, String value) {
        List<AuditResponse> auditResponses = auditEventService.findMetadata(name, value);
        return new ResponseEntity(auditResponses, HttpStatus.OK);
    }

    @GetMapping("/getMetadata")
    public ResponseEntity<AuditResponse> getMetadata() {
        List<AuditResponse> auditResponses = auditEventService.getMetadata();
        return new ResponseEntity(auditResponses, HttpStatus.OK);
    }

}
