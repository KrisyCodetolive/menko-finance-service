package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.request.CompteBancaireRequest;
import com.menko.comptabilite.application.port.in.CompteBancaireUseCase;
import com.menko.comptabilite.domain.model.CompteBancaire;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comptes-bancaires")
@RequiredArgsConstructor
public class CompteBancaireController {

    private final CompteBancaireUseCase compteBancaireUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CompteBancaire>>> lister(@RequestParam UUID filialeId) {
        return ResponseEntity.ok(ApiResponse.ok(
                compteBancaireUseCase.listerParFiliale(filialeId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompteBancaire>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(compteBancaireUseCase.trouverParId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CompteBancaire>> creer(
            @Valid @RequestBody CompteBancaireRequest request) {
        return ResponseEntity.status(201).body(
                ApiResponse.created(compteBancaireUseCase.creer(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CompteBancaire>> modifier(
            @PathVariable UUID id,
            @Valid @RequestBody CompteBancaireRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(compteBancaireUseCase.modifier(id, request)));
    }

    @PostMapping("/{id}/cloturer")
    public ResponseEntity<ApiResponse<Void>> cloturer(@PathVariable UUID id) {
        compteBancaireUseCase.cloturerCompte(id);
        return ResponseEntity.ok(ApiResponse.ok("Compte bancaire clôturé"));
    }
}
