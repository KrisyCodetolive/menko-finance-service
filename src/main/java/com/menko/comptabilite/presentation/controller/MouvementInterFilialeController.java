package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.PageResponse;
import com.menko.comptabilite.application.dto.request.MouvementInterFilialeRequest;
import com.menko.comptabilite.application.port.in.MouvementInterFilialeUseCase;
import com.menko.comptabilite.domain.model.MouvementInterFiliale;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mouvements-inter-filiales")
@RequiredArgsConstructor
public class MouvementInterFilialeController {

    private final MouvementInterFilialeUseCase mouvementUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MouvementInterFiliale>>> lister(
            @RequestParam UUID filialeId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(mouvementUseCase.listerParFiliale(filialeId, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MouvementInterFiliale>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(mouvementUseCase.trouverParId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MouvementInterFiliale>> enregistrer(
            @Valid @RequestBody MouvementInterFilialeRequest request) {
        UUID utilisateurId = UUID.fromString(
                (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return ResponseEntity.status(201).body(
                ApiResponse.created(mouvementUseCase.enregistrer(request, utilisateurId)));
    }
}
