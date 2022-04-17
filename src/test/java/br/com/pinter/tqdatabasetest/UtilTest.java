/*
 * Copyright (C) 2022 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabasetest;

import br.com.pinter.tqdatabase.Database;
import br.com.pinter.tqdatabase.cache.CacheDbRecord;
import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.models.DbVariable;
import br.com.pinter.tqdatabase.util.Util;
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
        List<DbVariable> dbVariables = Util.filterRecordVariables(r, "(?i)potionstacklimit");
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
                Util.normalizeRecordPath("records/game/gameengine.dbr"));
    }

    @Test
    void normalizeRecordPath_Given_null_Then_returnNull() {
        assertNull(Util.normalizeRecordPath(null));
    }

}