/*
 * Copyright (C) 2022 Emerson Pinter - All Rights Reserved
 */

/*    This file is part of TQ Database.

    TQ Database is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    TQ Database is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with TQ Database.  If not, see <http://www.gnu.org/licenses/>.
*/

package br.com.pinter.tqdatabase.data.dao;

import br.com.pinter.tqdatabase.data.DatabaseReader;
import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.models.Teleport;

public class TeleportDAO implements BaseDAO {
    private static final System.Logger logger = System.getLogger(TeleportDAO.class.getName());
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
