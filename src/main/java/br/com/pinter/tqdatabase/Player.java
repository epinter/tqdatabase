/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.models.Pc;
import br.com.pinter.tqdatabase.models.PlayerLevels;

public class Player implements TQService {
    private final PlayerLevelsDAO playerLevelsDAO;
    private final PcDAO pcDAO;

    public Player(DatabaseReader databaseReader) {
        playerLevelsDAO = new PlayerLevelsDAO(databaseReader);
        pcDAO = new PcDAO(databaseReader);
    }

    @Override
    public void preload() {
        playerLevelsDAO.preload();
        pcDAO.preload();
    }

    public PlayerLevels getPlayerLevels() {
        return playerLevelsDAO.getPlayerLevels();
    }

    public Pc getPc(Pc.Gender gender) {
        return pcDAO.getPc(gender);
    }

    public Pc getPc() {
        return pcDAO.getPc();
    }

}
