package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.PageResponse;
import com.menko.comptabilite.application.dto.request.CompteComptableRequest;
import com.menko.comptabilite.application.port.in.CompteComptableUseCase;
import com.menko.comptabilite.domain.model.CompteComptable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comptes-comptables")
@RequiredArgsConstructor
public class CompteComptableController {

    private final CompteComptableUseCase compteUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CompteComptable>>> lister(
            @RequestParam UUID filialeId,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(compteUseCase.listerParFiliale(filialeId, pageable))));
    }

    @GetMapping("/classe/{classe}")
    public ResponseEntity<ApiResponse<List<CompteComptable>>> listerParClasse(
            @RequestParam UUID filialeId,
            @PathVariable Integer classe) {
        return ResponseEntity.ok(ApiResponse.ok(
                compteUseCase.listerParFilialeEtClasse(filialeId, classe)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompteComptable>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(compteUseCase.trouverParId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CompteComptable>> creer(
            @Valid @RequestBody CompteComptableRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(compteUseCase.creer(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CompteComptable>> modifier(
            @PathVariable UUID id,
            @Valid @RequestBody CompteComptableRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(compteUseCase.modifier(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> archiver(@PathVariable UUID id) {
        compteUseCase.archiver(id);
        return ResponseEntity.ok(ApiResponse.ok("Compte archivé"));
    }
}
