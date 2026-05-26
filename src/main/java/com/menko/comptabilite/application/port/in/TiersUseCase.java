package com.menko.comptabilite.application.port.in;

import com.menko.comptabilite.application.dto.request.TiersRequest;
import com.menko.comptabilite.domain.model.Tiers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/** Gestion des clients et fournisseurs par filiale. */
public interface TiersUseCase {
    Page<Tiers> listerParFiliale(UUID filialeId, String type, Pageable pageable);
    Tiers trouverParId(UUID id);
    Tiers creer(TiersRequest request);
    Tiers modifier(UUID id, TiersRequest request);
    void supprimer(UUID id);
}
