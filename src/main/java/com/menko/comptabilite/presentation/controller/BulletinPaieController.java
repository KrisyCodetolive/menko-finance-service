package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.request.BulletinPaieRequest;
import com.menko.comptabilite.application.port.in.BulletinPaieUseCase;
import com.menko.comptabilite.domain.model.BulletinPaie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bulletins-paie")
@RequiredArgsConstructor
public class BulletinPaieController {

    private final BulletinPaieUseCase bulletinUseCase;

    @GetMapping("/employe/{employeId}")
    public ResponseEntity<ApiResponse<List<BulletinPaie>>> listerParEmploye(
            @PathVariable UUID employeId) {
        return ResponseEntity.ok(ApiResponse.ok(bulletinUseCase.listerParEmploye(employeId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BulletinPaie>>> listerParFilialeEtPeriode(
            @RequestParam UUID filialeId,
            @RequestParam int mois,
            @RequestParam int annee) {
        return ResponseEntity.ok(ApiResponse.ok(
                bulletinUseCase.listerParFilialeEtPeriode(filialeId, mois, annee)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BulletinPaie>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(bulletinUseCase.trouverParId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BulletinPaie>> creer(@Valid @RequestBody BulletinPaieRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(bulletinUseCase.creer(request)));
    }
}
