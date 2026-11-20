package tn.esprit.autoloc.web.controller;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.RoleEmploye;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEmployeRepository;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AffectationControllerTest {

    @Autowired
    private EmployeController employeController;

    @Autowired
    private VehiculeController vehiculeController;

    @Autowired
    private IAgenceRepository agenceRepository;

    @Autowired
    private IEmployeRepository employeRepository;

    @Autowired
    private IVehiculeRepository vehiculeRepository;

    @Autowired
    private IEquipementRepository equipementRepository;

    @Test
    @Transactional
    @DisplayName("Endpoint Test - Mutation Employé (PUT desaffecter puis PUT affecter)")
    void testControllerEmployeEndpoints() {
        Agence tunis = new Agence();
        tunis.setNom("Agence Tunis");
        tunis = agenceRepository.save(tunis);

        Employe employe = new Employe();
        employe.setNom("Ben Salah");
        employe.setPrenom("Ahmed");
        employe.setRole(RoleEmploye.AGENT);
        employe = employeRepository.save(employe);

        // 1. Affectation
        Employe affecte = employeController.affecterEmploye(employe.getIdEmploye(), tunis.getIdAgence());
        assertNotNull(affecte.getAgence());
        assertEquals(tunis.getIdAgence(), affecte.getAgence().getIdAgence());

        // 2. Désaffectation
        Employe desaffecte = employeController.desaffecterEmploye(employe.getIdEmploye());
        assertNull(desaffecte.getAgence());
    }

    @Test
    @Transactional
    @DisplayName("Endpoint Test - Véhicule & Equipements (PUT affecter agence, PUT affecter equipement, DELETE desaffecter equipement)")
    void testControllerVehiculeEndpoints() {
        Agence sfax = new Agence();
        sfax.setNom("Agence Sfax");
        sfax = agenceRepository.save(sfax);

        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation("245-TN-1001");
        vehicule.setMarque("Renault");
        vehicule.setModele("Clio 5");
        vehicule.setCategorie(CategorieVehicule.CITADINE);
        vehicule.setTarifJournalier(new BigDecimal("90.00"));
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        vehicule = vehiculeRepository.save(vehicule);

        Equipement siegeBebe = new Equipement();
        siegeBebe.setLibelle("Siège bébé");
        final Equipement savedSiegeBebe = equipementRepository.save(siegeBebe);
        final Long siegeBebeId = savedSiegeBebe.getIdEquipement();

        // Affecter véhicule à l'agence Sfax
        Vehicule vAvecAgence = vehiculeController.affecterVehicule(vehicule.getIdVehicule(), sfax.getIdAgence());
        assertNotNull(vAvecAgence.getAgence());
        assertEquals(sfax.getIdAgence(), vAvecAgence.getAgence().getIdAgence());

        // Affecter équipement (siège bébé)
        Vehicule vAvecEquip = vehiculeController.affecterEquipement(vehicule.getIdVehicule(), siegeBebeId);
        assertNotNull(vAvecEquip.getEquipements());
        assertTrue(vAvecEquip.getEquipements().stream().anyMatch(e -> e.getIdEquipement().equals(siegeBebeId)));

        // Désaffecter équipement
        Vehicule vSansEquip = vehiculeController.desaffecterEquipement(vehicule.getIdVehicule(), siegeBebeId);
        assertFalse(vSansEquip.getEquipements().stream().anyMatch(e -> e.getIdEquipement().equals(siegeBebeId)));
    }
}
