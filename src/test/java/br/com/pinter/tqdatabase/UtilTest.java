/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.cache.CacheDbRecord;
import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.models.DbVariable;
import br.com.pinter.tqdatabase.util.Util;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class UtilTest {
    private Database database;

    @Before
    public void init() throws IOException {
        CacheDbRecord.getInstance().clear();
        if(!new File("src/test/resources/database.arz").exists()) {
            throw new IOException("File src/test/resources/database.arz is missing," +
                    " copy the database to execute the tests");
        }
        database = new Database(new String[]{"src/test/resources/database.arz"});
    }

    @Test
    public void filterRecordVariables_Given_dbRecordRegexp_Then_returnMatchVariableList() {
        DbRecord r = database.getRecord("RECORDS\\GAME\\GAMEENGINE.DBR");
        List<DbVariable> dbVariables = Util.filterRecordVariables(r, "(?i)potionstacklimit");
        assertNotNull(dbVariables);
        assertFalse(dbVariables.isEmpty());
    }

    @Test
    public void filterRecordVariables_Given_invalidDbRecordRegexp_Then_returnNull() {
        DbRecord r = database.getRecord("xxx");
        assertNull(r);
    }

    @Test
    public void normalizeRecordPath_Given_Path_Then_returnNormalized() {
        assertEquals(Util.normalizeRecordPath("records/game/gameengine.dbr"),
                "RECORDS\\GAME\\GAMEENGINE.DBR");
    }

    @Test
    public void normalizeRecordPath_Given_null_Then_returnNull() {
        assertNull(Util.normalizeRecordPath(null));
    }

}