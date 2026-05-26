package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.PageResponse;
import com.menko.comptabilite.application.dto.request.EcritureComptableRequest;
import com.menko.comptabilite.application.port.in.EcritureComptableUseCase;
import com.menko.comptabilite.domain.model.EcritureComptable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ecritures")
@RequiredArgsConstructor
public class EcritureComptableController {

    private final EcritureComptableUseCase ecritureUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<EcritureComptable>>> lister(
            @RequestParam UUID filialeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(ecritureUseCase.listerParFiliale(filialeId, debut, fin, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EcritureComptable>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(ecritureUseCase.trouverParId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EcritureComptable>> creer(
            @Valid @RequestBody EcritureComptableRequest request) {
        UUID utilisateurId = UUID.fromString(
                (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return ResponseEntity.status(201).body(
                ApiResponse.created(ecritureUseCase.creer(request, utilisateurId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EcritureComptable>> modifier(
            @PathVariable UUID id,
            @Valid @RequestBody EcritureComptableRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(ecritureUseCase.modifier(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> supprimer(@PathVariable UUID id) {
        ecritureUseCase.supprimer(id);
        return ResponseEntity.ok(ApiResponse.ok("Écriture supprimée"));
    }
}
