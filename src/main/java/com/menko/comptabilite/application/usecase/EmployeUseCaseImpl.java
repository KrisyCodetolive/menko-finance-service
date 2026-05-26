package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.request.EmployeRequest;
import com.menko.comptabilite.application.port.in.EmployeUseCase;
import com.menko.comptabilite.application.port.out.EmployePort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import com.menko.comptabilite.domain.model.Employe;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeUseCaseImpl implements EmployeUseCase {

    private final EmployePort employePort;

    @Override
    public Page<Employe> listerParFiliale(UUID filialeId, Pageable pageable) {
        return employePort.findByFilialeId(filialeId, pageable);
    }

    @Override
    public Employe trouverParId(UUID id) {
        return employePort.findById(id)
                .orElseThrow(() -> new EntiteIntrouvableException("Employé", id));
    }

    @Override
    public Employe creer(EmployeRequest request) {
        return employePort.save(Employe.builder()
                .nom(request.nom())
                .prenom(request.prenom())
                .poste(request.poste())
                .salaireBase(request.salaireBase())
                .dateEmbauche(request.dateEmbauche())
                .actif(true)
                .identifiantFiliale(request.identifiantFiliale())
                .build());
    }

    @Override
    public Employe modifier(UUID id, EmployeRequest request) {
        Employe employe = trouverParId(id);
        employe.setNom(request.nom());
        employe.setPrenom(request.prenom());
        employe.setPoste(request.poste());
        employe.setSalaireBase(request.salaireBase());
        employe.setDateEmbauche(request.dateEmbauche());
        return employePort.save(employe);
    }

    @Override
    public void archiver(UUID id) {
        Employe employe = trouverParId(id);
        employe.setActif(false);
        employePort.save(employe);
    }
}
