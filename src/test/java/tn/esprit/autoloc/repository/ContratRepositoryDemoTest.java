package tn.esprit.autoloc.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.ModePaiement;
import tn.esprit.autoloc.domain.Paiement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ContratRepositoryDemoTest {

    @Autowired
    private IContratRepository contratRepository;

    @Autowired
    private IPaiementRepository paiementRepository;

    @Test
    @Transactional
    void testCrudOperationsAndCascade() {
        // 1. Création (persist) d'un contrat avec cascade sur ses paiements
        Contrat contrat = new Contrat();
        contrat.setDateSignature(LocalDate.of(2026, 10, 15));
        contrat.setMontantTotal(new BigDecimal("450.00"));
        contrat.setValide(true);

        List<Paiement> paiements = new ArrayList<>();
        Paiement p1 = new Paiement();
        p1.setMontant(new BigDecimal("200.00"));
        p1.setDatePaiement(LocalDate.of(2026, 10, 15));
        p1.setModePaiement(ModePaiement.CARTE);
        p1.setContrat(contrat);
        paiements.add(p1);

        Paiement p2 = new Paiement();
        p2.setMontant(new BigDecimal("250.00"));
        p2.setDatePaiement(LocalDate.of(2026, 10, 16));
        p2.setModePaiement(ModePaiement.ESPECES);
        p2.setContrat(contrat);
        paiements.add(p2);

        contrat.setPaiements(paiements);

        // Sauvegarde avec persist (idContrat est null)
        Contrat savedContrat = contratRepository.save(contrat);
        assertNotNull(savedContrat.getIdContrat());
        Long contratId = savedContrat.getIdContrat();

        // 2. Lecture (findById) avec Optional
        Optional<Contrat> optionalContrat = contratRepository.findById(contratId);
        assertTrue(optionalContrat.isPresent());
        assertEquals(new BigDecimal("450.00"), optionalContrat.get().getMontantTotal());

        // 3. Mise à jour (merge car idContrat est non-null)
        savedContrat.setMontantTotal(new BigDecimal("500.00"));
        Contrat updatedContrat = contratRepository.save(savedContrat);
        assertEquals(new BigDecimal("500.00"), updatedContrat.getMontantTotal());

        // 4. Test de pagination et tri (PagingAndSorting / JpaRepository)
        Page<Contrat> page = contratRepository.findAll(PageRequest.of(0, 10, Sort.by("montantTotal").descending()));
        assertFalse(page.isEmpty());

        // 5. Test suppression et cascade
        contratRepository.deleteById(contratId);
        Optional<Contrat> deletedContrat = contratRepository.findById(contratId);
        assertTrue(deletedContrat.isEmpty());
    }
}
