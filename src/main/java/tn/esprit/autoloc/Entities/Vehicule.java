package tn.esprit.autoloc.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Vehicule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVehicule;

    private String immatriculation;

    private String marque;

    private String modele;

    @Enumerated(EnumType.STRING)
    private CategorieVehicule categorie;

    private BigDecimal tarifJournalier;

    @Enumerated(EnumType.STRING)
    private StatutVehicule statut;

    @ManyToOne
    private Agence agence;

    @ManyToMany
    @JoinTable(
        name = "vehicule_equipement",
        joinColumns = @JoinColumn(name = "id_vehicule"),
        inverseJoinColumns = @JoinColumn(name = "id_equipement")
    )
    @ToString.Exclude
    private List<Equipement> equipements;

    @OneToMany(mappedBy = "vehicule")
    @ToString.Exclude
    private List<Maintenance> maintenances;

    @OneToMany(mappedBy = "vehicule")
    @ToString.Exclude
    private List<Reservation> reservations;
}
