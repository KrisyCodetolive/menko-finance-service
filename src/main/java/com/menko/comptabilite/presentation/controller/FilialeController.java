package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.request.FilialeRequest;
import com.menko.comptabilite.application.port.in.FilialeUseCase;
import com.menko.comptabilite.domain.model.Filiale;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Filiales", description = "Gestion des filiales du groupe Menko Holding")
@RestController
@RequestMapping("/api/v1/filiales")
@RequiredArgsConstructor
public class FilialeController {

    private final FilialeUseCase filialeUseCase;

    @Operation(summary = "Lister les filiales", description = "Retourne toutes les filiales du groupe")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Filiale>>> lister() {
        return ResponseEntity.ok(ApiResponse.ok(filialeUseCase.listerToutes()));
    }

    @Operation(summary = "Obtenir une filiale", description = "Retourne les détails d'une filiale")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Filiale>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(filialeUseCase.trouverParId(id)));
    }

    @Operation(summary = "Créer une filiale", description = "Crée une nouvelle filiale dans le groupe. Accès réservé au SUPER_ADMIN")
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Filiale>> creer(@Valid @RequestBody FilialeRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(filialeUseCase.creer(request)));
    }

    @Operation(summary = "Modifier une filiale", description = "Met à jour les informations d'une filiale. Accès réservé au SUPER_ADMIN")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Filiale>> modifier(
            @PathVariable UUID id,
            @Valid @RequestBody FilialeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(filialeUseCase.modifier(id, request)));
    }
}
