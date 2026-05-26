package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.PageResponse;
import com.menko.comptabilite.application.dto.request.EmployeRequest;
import com.menko.comptabilite.application.port.in.EmployeUseCase;
import com.menko.comptabilite.domain.model.Employe;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Employés", description = "Gestion des employés des filiales")
@RestController
@RequestMapping("/api/v1/employes")
@RequiredArgsConstructor
public class EmployeController {

    private final EmployeUseCase employeUseCase;

    @Operation(summary = "Lister les employés", description = "Employés actifs d'une filiale avec pagination")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Employe>>> lister(
            @RequestParam UUID filialeId,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(employeUseCase.listerParFiliale(filialeId, pageable))));
    }

    @Operation(summary = "Obtenir un employé", description = "Retourne les détails d'un employé")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Employe>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(employeUseCase.trouverParId(id)));
    }

    @Operation(summary = "Créer un employé", description = "Enregistre un nouvel employé dans la filiale")
    @PostMapping
    public ResponseEntity<ApiResponse<Employe>> creer(@Valid @RequestBody EmployeRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(employeUseCase.creer(request)));
    }

    @Operation(summary = "Modifier un employé", description = "Met à jour les informations d'un employé")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Employe>> modifier(
            @PathVariable UUID id,
            @Valid @RequestBody EmployeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(employeUseCase.modifier(id, request)));
    }

    @Operation(summary = "Archiver un employé", description = "Archive un employé (pas de suppression physique)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> archiver(@PathVariable UUID id) {
        employeUseCase.archiver(id);
        return ResponseEntity.ok(ApiResponse.ok("Employé archivé"));
    }
}
