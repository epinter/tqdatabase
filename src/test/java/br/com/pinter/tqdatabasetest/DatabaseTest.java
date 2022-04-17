/*
 * Copyright (C) 2022 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabasetest;

import br.com.pinter.tqdatabase.Database;
import br.com.pinter.tqdatabase.Skills;
import br.com.pinter.tqdatabase.cache.CacheDbRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
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
    void getSkillDAO_Should_returnSkillDAO() {
        Skills skills = database.skills();
        assertNotNull(skills);
    }
}