package com.menko.comptabilite.application.port.in;

import com.menko.comptabilite.application.dto.request.MouvementInterFilialeRequest;
import com.menko.comptabilite.domain.model.MouvementInterFiliale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/** Virements inter-filiales avec double écriture automatique. */
public interface MouvementInterFilialeUseCase {
    Page<MouvementInterFiliale> listerParFiliale(UUID filialeId, Pageable pageable);
    MouvementInterFiliale trouverParId(UUID id);
    /** Crée le mouvement ET les deux écritures comptables correspondantes. */
    MouvementInterFiliale enregistrer(MouvementInterFilialeRequest request, UUID utilisateurId);
}
