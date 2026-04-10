package com.menko.comptabilite.controller;

import com.menko.comptabilite.dto.CompteComptableDTO;
import com.menko.comptabilite.service.CompteComptableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comptes")
@RequiredArgsConstructor
public class CompteComptableController {

    private final CompteComptableService service;

    @GetMapping
    public ResponseEntity<List<CompteComptableDTO>> listerTous() {
        return ResponseEntity.ok(service.listerTous());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompteComptableDTO> trouverParId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.trouverParId(id));
    }

    @GetMapping("/filiale/{identifiantFiliale}")
    public ResponseEntity<List<CompteComptableDTO>> listerParFiliale(@PathVariable UUID identifiantFiliale) {
        return ResponseEntity.ok(service.listerParFiliale(identifiantFiliale));
    }

    @PostMapping
    public ResponseEntity<CompteComptableDTO> creer(@Valid @RequestBody CompteComptableDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.creer(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompteComptableDTO> modifier(@PathVariable UUID id, @Valid @RequestBody CompteComptableDTO dto) {
        return ResponseEntity.ok(service.modifier(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> archiver(@PathVariable UUID id) {
        service.archiver(id);
        return ResponseEntity.noContent().build();
    }
}
