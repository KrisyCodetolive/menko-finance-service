package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.response.DashboardFilialeResponse;
import com.menko.comptabilite.application.port.in.DashboardUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Dashboard financier", description = "Indicateurs financiers consolidés par filiale et pour le groupe")
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardUseCase dashboardUseCase;

    @Operation(summary = "Dashboard filiale", description = "Indicateurs clés d'une filiale : trésorerie (classe 5), total débit/crédit, chèques en attente d'encaissement")
    @GetMapping("/filiale/{filialeId}")
    public ResponseEntity<ApiResponse<DashboardFilialeResponse>> filiale(
            @PathVariable UUID filialeId) {
        return ResponseEntity.ok(ApiResponse.ok(dashboardUseCase.dashboardFiliale(filialeId)));
    }

    @Operation(summary = "Dashboard groupe", description = "Consolidation des indicateurs de toutes les filiales du groupe. Accès restreint SUPER_ADMIN et DG")
    @GetMapping("/groupe")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'DG')")
    public ResponseEntity<ApiResponse<List<DashboardFilialeResponse>>> groupe() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardUseCase.dashboardGroupe()));
    }
}
