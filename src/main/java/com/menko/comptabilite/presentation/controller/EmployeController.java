package com.menko.comptabilite.presentation.controller;

import com.menko.comptabilite.application.dto.ApiResponse;
import com.menko.comptabilite.application.dto.PageResponse;
import com.menko.comptabilite.application.dto.request.EmployeRequest;
import com.menko.comptabilite.application.port.in.EmployeUseCase;
import com.menko.comptabilite.domain.model.Employe;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employes")
@RequiredArgsConstructor
public class EmployeController {

    private final EmployeUseCase employeUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Employe>>> lister(
            @RequestParam UUID filialeId,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(employeUseCase.listerParFiliale(filialeId, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Employe>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(employeUseCase.trouverParId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Employe>> creer(@Valid @RequestBody EmployeRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(employeUseCase.creer(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Employe>> modifier(
            @PathVariable UUID id,
            @Valid @RequestBody EmployeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(employeUseCase.modifier(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> archiver(@PathVariable UUID id) {
        employeUseCase.archiver(id);
        return ResponseEntity.ok(ApiResponse.ok("Employé archivé"));
    }
}
