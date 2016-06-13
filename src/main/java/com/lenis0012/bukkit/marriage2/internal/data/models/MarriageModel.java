package com.lenis0012.bukkit.marriage2.internal.data.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "marriage_players")
public class MarriageModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, updatable = false)
    private int id;

    @Column(updatable = false, length = 128)
    private String player1;

    @Column(updatable = false, length = 128)
    private String player2;

    @ManyToMany
    @JoinTable
    private List<PlayerModel> players;

    @Column
    private boolean pvpEnabled = true;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="home_id")
    private LocationModel home;

    @Column
    private Timestamp marriedAt = new Timestamp(System.currentTimeMillis());

    @Version
    private long version;
}
