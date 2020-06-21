/*
 * Copyright (C) 2020 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.models.DbVariable;
import br.com.pinter.tqdatabase.models.Teleport;
import br.com.pinter.tqdatabase.util.Util;

import java.util.List;

public class TeleportDAO implements BaseDAO{
    private ArzFile arzFile;
    private final System.Logger logger = Util.getLogger(TeleportDAO.class.getName());

    public TeleportDAO(ArzFile arzFile) {
        this.arzFile = arzFile;
    }

    @Override
    public DbRecord getRecord(String recordPath) {
        return arzFile.getRecord(recordPath);
    }

    @Override
    public void preload() {
    }

    public Teleport getTeleport(String recordId) {
        DbRecord tpRecord = getRecord(recordId);
        String description = (String) tpRecord.getFirstValue("description");
        String className = (String) tpRecord.getFirstValue("Class");
        if(description!=null) {
            Teleport teleport = new Teleport();
            teleport.setDescription(description);
            teleport.setClassName(className);
            return teleport;
        }

        return null;
    }
}
