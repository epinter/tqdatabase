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

package br.com.pinter.tqdatabasetest;

import br.com.pinter.tqdatabase.Database;
import br.com.pinter.tqdatabase.cache.CacheDbRecord;
import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.models.DbVariable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {
    private Database database;

    @BeforeEach
    void init() throws IOException {
        CacheDbRecord.getInstance().clear();
        if(!new File("src/test/resources/database.arz").exists()) {
            throw new IOException("File src/test/resources/database.arz is missing," +
                    " copy the database to execute the tests");
        }
        database = new Database(new String[]{"src/test/resources/database.arz"});
    }

    @Test
    void filterRecordVariables_Given_dbRecordRegexp_Then_returnMatchVariableList() {
        DbRecord r = database.getRecord("RECORDS\\GAME\\GAMEENGINE.DBR");
        List<DbVariable> dbVariables = DbVariable.filterRecordVariables(r, "(?i)potionstacklimit");
        assertNotNull(dbVariables);
        assertFalse(dbVariables.isEmpty());
    }

    @Test
     void filterRecordVariables_Given_invalidDbRecordRegexp_Then_returnNull() {
        DbRecord r = database.getRecord("xxx");
        assertNull(r);
    }

    @Test
    void normalizeRecordPath_Given_Path_Then_returnNormalized() {
        assertEquals("RECORDS\\GAME\\GAMEENGINE.DBR",
                DbRecord.normalizeRecordPath("records/game/gameengine.dbr"));
    }

    @Test
    void normalizeRecordPath_Given_null_Then_returnNull() {
        assertNull(DbRecord.normalizeRecordPath(null));
    }

}