package tn.esprit.autoloc.service;

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
class AffectationScenariosTest {

    @Autowired
    private IEmployeService employeService;

    @Autowired
    private IVehiculeService vehiculeService;

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
    @DisplayName("1. Scénario RH : Muter un agent vers l'agence de Tunis (Désaffectation puis Affectation)")
    void testScenario1_MuterAgentVersAgenceTunis() {
        // Agence de départ (ex: Agence Sousse)
        Agence agenceDepart = new Agence();
        agenceDepart.setNom("Agence Sousse");
        agenceDepart.setVille("Sousse");
        agenceDepart.setAdresse("Avenue Habib Bourguiba");
        agenceDepart.setTelephone("73000000");
        agenceDepart = agenceRepository.save(agenceDepart);

        // Agence de destination (Agence Tunis)
        Agence agenceTunis = new Agence();
        agenceTunis.setNom("Agence Tunis");
        agenceTunis.setVille("Tunis");
        agenceTunis.setAdresse("Centre Urbain Nord");
        agenceTunis.setTelephone("71000000");
        agenceTunis = agenceRepository.save(agenceTunis);

        // Employé initialement rattaché à l'agence de départ
        Employe employe = new Employe();
        employe.setNom("Ben Salah");
        employe.setPrenom("Ahmed");
        employe.setRole(RoleEmploye.AGENT);
        employe.setAgence(agenceDepart);
        employe = employeRepository.save(employe);

        assertNotNull(employe.getIdEmploye());
        assertEquals(agenceDepart.getIdAgence(), employe.getAgence().getIdAgence());

        // Étape 1 : Désaffectation
        Employe employeDesaffecte = employeService.desaffecterEmploye(employe.getIdEmploye());
        assertNull(employeDesaffecte.getAgence(), "L'employé ne doit plus avoir d'agence après désaffectation");

        // Étape 2 : Affectation vers la nouvelle agence (Tunis)
        Employe employeMute = employeService.affecterEmploye(employe.getIdEmploye(), agenceTunis.getIdAgence());
        assertNotNull(employeMute.getAgence());
        assertEquals(agenceTunis.getIdAgence(), employeMute.getAgence().getIdAgence());
        assertEquals("Agence Tunis", employeMute.getAgence().getNom());
    }

    @Test
    @Transactional
    @DisplayName("2. Scénario Véhicule Neuf : Rattachement à l'agence de Sfax")
    void testScenario2_AffecterVehiculeNeufVersAgenceSfax() {
        // Création de l'agence de Sfax
        Agence agenceSfax = new Agence();
        agenceSfax.setNom("Agence Sfax");
        agenceSfax.setVille("Sfax");
        agenceSfax.setAdresse("Route de Téniour");
        agenceSfax.setTelephone("74000000");
        agenceSfax = agenceRepository.save(agenceSfax);

        // Arrivée d'un nouveau véhicule sans agence
        Vehicule vehiculeNeuf = new Vehicule();
        vehiculeNeuf.setImmatriculation("245-TN-1001");
        vehiculeNeuf.setMarque("Renault");
        vehiculeNeuf.setModele("Clio 5");
        vehiculeNeuf.setCategorie(CategorieVehicule.CITADINE);
        vehiculeNeuf.setTarifJournalier(new BigDecimal("90.00"));
        vehiculeNeuf.setStatut(StatutVehicule.DISPONIBLE);
        vehiculeNeuf.setAgence(null);
        vehiculeNeuf = vehiculeRepository.save(vehiculeNeuf);

        assertNull(vehiculeNeuf.getAgence());

        // Rattachement du véhicule à l'agence de Sfax
        Vehicule vehiculeAffecte = vehiculeService.affecterVehicule(vehiculeNeuf.getIdVehicule(), agenceSfax.getIdAgence());

        assertNotNull(vehiculeAffecte.getAgence());
        assertEquals(agenceSfax.getIdAgence(), vehiculeAffecte.getAgence().getIdAgence());
        assertEquals("Agence Sfax", vehiculeAffecte.getAgence().getNom());
    }

    @Test
    @Transactional
    @DisplayName("3. Scénario Equipement : Installation d'un siège bébé puis retrait au retour")
    void testScenario3_AffecterPuisDesaffecterSiegeBebe() {
        // Véhicule
        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation("230-TN-5555");
        vehicule.setMarque("Peugeot");
        vehicule.setModele("208");
        vehicule.setCategorie(CategorieVehicule.CITADINE);
        vehicule.setTarifJournalier(new BigDecimal("95.00"));
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        vehicule = vehiculeRepository.save(vehicule);

        // Equipement : Siège bébé
        Equipement siegeBebe = new Equipement();
        siegeBebe.setLibelle("Siège bébé");
        final Equipement savedSiegeBebe = equipementRepository.save(siegeBebe);
        final Long siegeBebeId = savedSiegeBebe.getIdEquipement();

        // Étape 1 : Le client demande un siège bébé -> L'agent l'installe
        Vehicule vehiculeAvecEquipement = vehiculeService.affecterEquipement(vehicule.getIdVehicule(), siegeBebeId);
        assertNotNull(vehiculeAvecEquipement.getEquipements());
        assertTrue(vehiculeAvecEquipement.getEquipements().stream()
                .anyMatch(e -> e.getIdEquipement().equals(siegeBebeId)),
                "Le véhicule doit contenir le siège bébé");

        // Étape 2 : Au retour, l'agent retire le siège bébé
        Vehicule vehiculeSansEquipement = vehiculeService.desaffecterEquipement(vehicule.getIdVehicule(), siegeBebeId);
        assertNotNull(vehiculeSansEquipement.getEquipements());
        assertFalse(vehiculeSansEquipement.getEquipements().stream()
                .anyMatch(e -> e.getIdEquipement().equals(siegeBebeId)),
                "Le siège bébé doit avoir été retiré du véhicule");
    }
}
