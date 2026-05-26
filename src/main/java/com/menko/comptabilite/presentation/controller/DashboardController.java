package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.response.DashboardFilialeResponse;
import com.menko.comptabilite.application.port.in.DashboardUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardUseCase dashboardUseCase;

    @GetMapping("/filiale/{filialeId}")
    public ResponseEntity<ApiResponse<DashboardFilialeResponse>> filiale(
            @PathVariable UUID filialeId) {
        return ResponseEntity.ok(ApiResponse.ok(dashboardUseCase.dashboardFiliale(filialeId)));
    }

    @GetMapping("/groupe")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'DG')")
    public ResponseEntity<ApiResponse<List<DashboardFilialeResponse>>> groupe() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardUseCase.dashboardGroupe()));
    }
}
