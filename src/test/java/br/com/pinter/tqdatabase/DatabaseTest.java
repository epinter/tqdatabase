/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.cache.CacheDbRecord;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class DatabaseTest {
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
    public void getSkillDAO_Should_returnSkillDAO() {
        Skills skills = database.skills();
        assertNotNull(skills);
    }
}