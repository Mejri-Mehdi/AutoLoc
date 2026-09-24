package tn.esprit.autoloc.Entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Vehicule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long idvehicule;
    String immatriculation;
    String marque;
    String modele;
    @Enumerated(EnumType.STRING)
    CategorieVehicule categories ;
    BigDecimal tarifjournaliere;
    StatutVehicule statut;
}
