/*
 * Copyright (C) 2020 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.models.Teleport;

public class Teleports implements ITQService {
    final private TeleportDAO teleportDAO;

    @Override
    public void preload() {
    }

    Teleports(ArzFile arzFile) {
        teleportDAO = new TeleportDAO(arzFile);
    }

    public Teleport getTeleport(String recordPath) {
        return teleportDAO.getTeleport(recordPath);
    }
}
