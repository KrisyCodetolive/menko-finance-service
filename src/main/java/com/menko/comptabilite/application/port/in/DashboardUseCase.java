package com.menko.comptabilite.application.port.in;

import com.menko.comptabilite.application.dto.response.DashboardFilialeResponse;

import java.util.List;
import java.util.UUID;

/** Tableau de bord consolidé par filiale. */
public interface DashboardUseCase {
    DashboardFilialeResponse dashboardFiliale(UUID filialeId);
    List<DashboardFilialeResponse> dashboardGroupe();
}
