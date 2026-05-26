package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.request.JournalComptableRequest;
import com.menko.comptabilite.application.port.in.JournalComptableUseCase;
import com.menko.comptabilite.domain.model.JournalComptable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/journaux")
@RequiredArgsConstructor
public class JournalComptableController {

    private final JournalComptableUseCase journalUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<JournalComptable>>> lister(@RequestParam UUID filialeId) {
        return ResponseEntity.ok(ApiResponse.ok(journalUseCase.listerParFiliale(filialeId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JournalComptable>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(journalUseCase.trouverParId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<JournalComptable>> creer(
            @Valid @RequestBody JournalComptableRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(journalUseCase.creer(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JournalComptable>> modifier(
            @PathVariable UUID id,
            @Valid @RequestBody JournalComptableRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(journalUseCase.modifier(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> supprimer(@PathVariable UUID id) {
        journalUseCase.supprimer(id);
        return ResponseEntity.ok(ApiResponse.ok("Journal supprimé"));
    }
}
