package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.request.FilialeRequest;
import com.menko.comptabilite.application.port.in.FilialeUseCase;
import com.menko.comptabilite.domain.model.Filiale;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/filiales")
@RequiredArgsConstructor
public class FilialeController {

    private final FilialeUseCase filialeUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Filiale>>> lister() {
        return ResponseEntity.ok(ApiResponse.ok(filialeUseCase.listerToutes()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Filiale>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(filialeUseCase.trouverParId(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Filiale>> creer(@Valid @RequestBody FilialeRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(filialeUseCase.creer(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Filiale>> modifier(
            @PathVariable UUID id,
            @Valid @RequestBody FilialeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(filialeUseCase.modifier(id, request)));
    }
}
