package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.PageResponse;
import com.menko.comptabilite.application.dto.request.TiersRequest;
import com.menko.comptabilite.application.port.in.TiersUseCase;
import com.menko.comptabilite.domain.model.Tiers;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tiers")
@RequiredArgsConstructor
public class TiersController {

    private final TiersUseCase tiersUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Tiers>>> lister(
            @RequestParam UUID filialeId,
            @RequestParam(required = false) String type,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(tiersUseCase.listerParFiliale(filialeId, type, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Tiers>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(tiersUseCase.trouverParId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Tiers>> creer(@Valid @RequestBody TiersRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(tiersUseCase.creer(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Tiers>> modifier(
            @PathVariable UUID id,
            @Valid @RequestBody TiersRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(tiersUseCase.modifier(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> supprimer(@PathVariable UUID id) {
        tiersUseCase.supprimer(id);
        return ResponseEntity.ok(ApiResponse.ok("Tiers supprimé"));
    }
}
