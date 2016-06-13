package com.lenis0012.bukkit.marriage2.internal.data.models;

import javax.persistence.ManyToMany;
import java.util.List;

public class PlayerModel {

    @ManyToMany(mappedBy = "players")
    private List<MarriageModel> marriages;
}
