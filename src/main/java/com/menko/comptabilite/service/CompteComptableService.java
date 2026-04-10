package com.menko.comptabilite.service;

import com.menko.comptabilite.dto.CompteComptableDTO;
import com.menko.comptabilite.entity.CompteComptable;
import com.menko.comptabilite.entity.Filiale;
import com.menko.comptabilite.repository.CompteComptableRepository;
import com.menko.comptabilite.repository.FilialeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompteComptableService {

    private final CompteComptableRepository repository;
    private final FilialeRepository filialeRepository;

    public List<CompteComptableDTO> listerTous() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CompteComptableDTO> listerParFiliale(UUID identifiantFiliale) {
        return repository.findByFilialeIdentifiant(identifiantFiliale).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CompteComptableDTO trouverParId(UUID id) {
        return toDTO(trouverEntite(id));
    }

    public CompteComptableDTO creer(CompteComptableDTO dto) {
        Filiale filiale = filialeRepository.findById(dto.getIdentifiantFiliale())
                .orElseThrow(() -> new EntityNotFoundException("Filiale introuvable : " + dto.getIdentifiantFiliale()));

        String numero = genererNumero(dto.getClasse(), dto.getIdentifiantFiliale());

        CompteComptable compte = new CompteComptable();
        compte.setNumero(numero);
        compte.setLibelle(dto.getLibelle());
        compte.setClasse(dto.getClasse());
        compte.setType(dto.getType());
        compte.setActif(true);
        compte.setFiliale(filiale);

        return toDTO(repository.save(compte));
    }

    public CompteComptableDTO modifier(UUID id, CompteComptableDTO dto) {
        CompteComptable compte = trouverEntite(id);
        compte.setLibelle(dto.getLibelle());
        compte.setType(dto.getType());
        if (dto.getActif() != null) {
            compte.setActif(dto.getActif());
        }
        return toDTO(repository.save(compte));
    }

    public void archiver(UUID id) {
        CompteComptable compte = trouverEntite(id);
        compte.setActif(false);
        repository.save(compte);
    }

    private String genererNumero(Integer classe, UUID identifiantFiliale) {
        String prefixe = String.valueOf(classe);
        String dernierNumero = repository
                .findMaxNumeroByClasseAndFiliale(classe, identifiantFiliale)
                .orElse(prefixe + "00000");

        int dernierInt = Integer.parseInt(dernierNumero);
        int suivant = dernierInt + 1;

        if (!String.valueOf(suivant).startsWith(prefixe)) {
            throw new IllegalStateException("Impossible de générer un numéro pour la classe " + classe);
        }

        return String.valueOf(suivant);
    }

    private CompteComptable trouverEntite(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Compte comptable introuvable : " + id));
    }

    private CompteComptableDTO toDTO(CompteComptable compte) {
        CompteComptableDTO dto = new CompteComptableDTO();
        dto.setIdentifiant(compte.getIdentifiant());
        dto.setNumero(compte.getNumero());
        dto.setLibelle(compte.getLibelle());
        dto.setClasse(compte.getClasse());
        dto.setType(compte.getType());
        dto.setActif(compte.getActif());
        dto.setIdentifiantFiliale(compte.getFiliale().getIdentifiant());
        dto.setNomFiliale(compte.getFiliale().getNom());
        return dto;
    }
}
