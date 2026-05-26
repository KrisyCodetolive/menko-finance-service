package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.response.BalanceLigneResponse;
import com.menko.comptabilite.application.dto.response.BilanResponse;
import com.menko.comptabilite.application.dto.response.GrandLivreLigneResponse;
import com.menko.comptabilite.application.port.in.BalanceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/editions")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceUseCase balanceUseCase;

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<List<BalanceLigneResponse>>> balance(
            @RequestParam UUID filialeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(ApiResponse.ok(balanceUseCase.balanceGenerale(filialeId, debut, fin)));
    }

    @GetMapping("/grand-livre")
    public ResponseEntity<ApiResponse<List<GrandLivreLigneResponse>>> grandLivre(
            @RequestParam UUID compteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(ApiResponse.ok(balanceUseCase.grandLivre(compteId, debut, fin)));
    }

    @GetMapping("/bilan")
    public ResponseEntity<ApiResponse<BilanResponse>> bilan(
            @RequestParam UUID filialeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(ApiResponse.ok(balanceUseCase.bilan(filialeId, fin)));
    }

    @GetMapping("/compte-de-resultat")
    public ResponseEntity<ApiResponse<List<BalanceLigneResponse>>> compteDeResultat(
            @RequestParam UUID filialeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(ApiResponse.ok(
                balanceUseCase.compteDeResultat(filialeId, debut, fin)));
    }
}
