/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.models.Teleport;
import br.com.pinter.tqdatabase.util.Util;

public class TeleportDAO implements BaseDAO{
    private final System.Logger logger = Util.getLogger(TeleportDAO.class.getName());

    private final DatabaseReader databaseReader;

    public TeleportDAO(DatabaseReader databaseReader) {
        this.databaseReader = databaseReader;
    }

    @Override
    public DatabaseReader getDatabaseReader() {
        return databaseReader;
    }

    @Override
    public void preload() {
        //unused
    }

    public Teleport getTeleport(String recordId) {
        DbRecord tpRecord = getRecord(recordId);
        String description = (String) tpRecord.getFirstValue("description");
        String className = (String) tpRecord.getFirstValue("Class");
        if(description!=null) {
            Teleport teleport = new Teleport();
            teleport.setDescription(description);
            teleport.setClassName(className);
            logger.log(System.Logger.Level.DEBUG, "teleport: found ''{0}''", teleport);
            return teleport;
        }

        return null;
    }
}
