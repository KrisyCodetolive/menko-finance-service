package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.response.BalanceLigneResponse;
import com.menko.comptabilite.application.dto.response.BilanResponse;
import com.menko.comptabilite.application.dto.response.GrandLivreLigneResponse;
import com.menko.comptabilite.application.port.in.BalanceUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Éditions comptables", description = "Balance générale, grand livre, bilan et compte de résultat SYSCOHADA")
@RestController
@RequestMapping("/api/v1/editions")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceUseCase balanceUseCase;

    @Operation(summary = "Balance générale", description = "Calcule la balance des comptes par filiale avec totaux débit/crédit et soldes débiteur/créditeur. Filtre sur les écritures VALIDEE et CLOTUREE")
    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<List<BalanceLigneResponse>>> balance(
            @RequestParam UUID filialeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(ApiResponse.ok(balanceUseCase.balanceGenerale(filialeId, debut, fin)));
    }

    @Operation(summary = "Grand livre", description = "Détail chronologique des mouvements d'un compte avec calcul du solde progressif ligne par ligne")
    @GetMapping("/grand-livre")
    public ResponseEntity<ApiResponse<List<GrandLivreLigneResponse>>> grandLivre(
            @RequestParam UUID compteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(ApiResponse.ok(balanceUseCase.grandLivre(compteId, debut, fin)));
    }

    @Operation(summary = "Bilan comptable", description = "Bilan à une date donnée : actif (immobilisations, stocks, créances, trésorerie) et passif (capitaux propres, dettes). Basé sur les classes SYSCOHADA 1-5")
    @GetMapping("/bilan")
    public ResponseEntity<ApiResponse<BilanResponse>> bilan(
            @RequestParam UUID filialeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(ApiResponse.ok(balanceUseCase.bilan(filialeId, fin)));
    }

    @Operation(summary = "Compte de résultat", description = "Charges (classe 6) et produits (classe 7) sur une période. Permet de calculer le résultat net de la filiale")
    @GetMapping("/compte-de-resultat")
    public ResponseEntity<ApiResponse<List<BalanceLigneResponse>>> compteDeResultat(
            @RequestParam UUID filialeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(ApiResponse.ok(
                balanceUseCase.compteDeResultat(filialeId, debut, fin)));
    }
}
