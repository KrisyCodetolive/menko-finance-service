package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.request.BulletinPaieRequest;
import com.menko.comptabilite.application.port.in.BulletinPaieUseCase;
import com.menko.comptabilite.domain.model.BulletinPaie;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Bulletins de paie", description = "Gestion des bulletins de paie des employés")
@RestController
@RequestMapping("/api/v1/bulletins-paie")
@RequiredArgsConstructor
public class BulletinPaieController {

    private final BulletinPaieUseCase bulletinUseCase;

    @Operation(summary = "Bulletins par employé", description = "Retourne tous les bulletins de paie d'un employé")
    @GetMapping("/employe/{employeId}")
    public ResponseEntity<ApiResponse<List<BulletinPaie>>> listerParEmploye(
            @PathVariable UUID employeId) {
        return ResponseEntity.ok(ApiResponse.ok(bulletinUseCase.listerParEmploye(employeId)));
    }

    @Operation(summary = "Bulletins par filiale et période", description = "Filtre les bulletins de paie par filiale, mois (1-12) et année")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BulletinPaie>>> listerParFilialeEtPeriode(
            @RequestParam UUID filialeId,
            @RequestParam int mois,
            @RequestParam int annee) {
        return ResponseEntity.ok(ApiResponse.ok(
                bulletinUseCase.listerParFilialeEtPeriode(filialeId, mois, annee)));
    }

    @Operation(summary = "Obtenir un bulletin", description = "Retourne un bulletin de paie par son identifiant")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BulletinPaie>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(bulletinUseCase.trouverParId(id)));
    }

    @Operation(summary = "Créer un bulletin", description = "Enregistre un bulletin de paie et génère automatiquement l'écriture comptable de paie au journal SAL")
    @PostMapping
    public ResponseEntity<ApiResponse<BulletinPaie>> creer(@Valid @RequestBody BulletinPaieRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(bulletinUseCase.creer(request)));
    }
}
