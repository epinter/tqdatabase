/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.models.Pc;
import br.com.pinter.tqdatabase.models.PlayerLevels;

public class Player implements ITQService {
    private PlayerLevelsDAO playerLevelsDAO;
    private PcDAO pcDAO;

    public Player(ArzFile arzFile) {
        playerLevelsDAO = new PlayerLevelsDAO(arzFile);
        pcDAO = new PcDAO(arzFile);
    }

    @Override
    public void preload() {
        playerLevelsDAO.preload();
        pcDAO.preload();
    }

    public PlayerLevels getPlayerLevels() {
        return playerLevelsDAO.getPlayerLevels();
    }

    public Pc getPc() {
        return pcDAO.getPc();
    }
}
