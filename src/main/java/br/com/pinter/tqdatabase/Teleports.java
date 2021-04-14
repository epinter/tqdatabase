/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.models.Teleport;

public class Teleports implements TQService {
    final private TeleportDAO teleportDAO;

    @Override
    public void preload() {
    }

    Teleports(DatabaseReader databaseReader) {
        teleportDAO = new TeleportDAO(databaseReader);
    }

    public Teleport getTeleport(String recordPath) {
        return teleportDAO.getTeleport(recordPath);
    }
}
