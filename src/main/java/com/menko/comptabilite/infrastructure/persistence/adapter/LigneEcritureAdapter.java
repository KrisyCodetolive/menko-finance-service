package com.menko.comptabilite.infrastructure.persistence.adapter;

import com.menko.comptabilite.application.dto.response.BalanceLigneResponse;
import com.menko.comptabilite.application.dto.response.GrandLivreLigneResponse;
import com.menko.comptabilite.application.port.out.LigneEcriturePort;
import com.menko.comptabilite.domain.model.LigneEcriture;
import com.menko.comptabilite.infrastructure.persistence.repository.LigneEcritureJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LigneEcritureAdapter implements LigneEcriturePort {

    private final LigneEcritureJpaRepository repository;

    @Override
    public List<LigneEcriture> findByEcritureId(UUID ecritureId) {
        return repository.findByEcritureIdentifiant(ecritureId).stream()
                .map(l -> LigneEcriture.builder()
                        .identifiant(l.getIdentifiant())
                        .identifiantEcriture(l.getEcriture().getIdentifiant())
                        .identifiantCompte(l.getCompte().getIdentifiant())
                        .numeroCompte(l.getCompte().getNumero())
                        .libelleCompte(l.getCompte().getLibelle())
                        .debit(l.getDebit())
                        .credit(l.getCredit())
                        .build())
                .toList();
    }

    @Override
    public LigneEcriture save(LigneEcriture ligne) {
        throw new UnsupportedOperationException("Les lignes sont sauvegardées via EcritureComptableAdapter");
    }

    @Override
    @Transactional
    public void deleteByEcritureId(UUID ecritureId) {
        repository.deleteByEcritureIdentifiant(ecritureId);
    }

    @Override
    public List<BalanceLigneResponse> calculerBalance(UUID filialeId, LocalDate debut, LocalDate fin) {
        return repository.calculerBalance(filialeId, debut, fin);
    }

    @Override
    public List<GrandLivreLigneResponse> calculerGrandLivre(UUID compteId, LocalDate debut, LocalDate fin) {
        List<GrandLivreLigneResponse> raw = repository.calculerGrandLivreRaw(compteId, debut, fin);
        List<GrandLivreLigneResponse> result = new ArrayList<>();
        BigDecimal solde = BigDecimal.ZERO;
        for (GrandLivreLigneResponse r : raw) {
            solde = solde.add(r.debit()).subtract(r.credit());
            result.add(new GrandLivreLigneResponse(
                    r.dateEcriture(), r.numeroPiece(), r.libelle(),
                    r.codeJournal(), r.debit(), r.credit(), solde));
        }
        return result;
    }
}
