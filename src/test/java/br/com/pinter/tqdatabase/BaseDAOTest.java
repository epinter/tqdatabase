/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.Database;
import br.com.pinter.tqdatabase.Skills;
import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.models.Pet;
import br.com.pinter.tqdatabase.models.Skill;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class BaseDAOTest {
    private Skills skills;
    private DbRecord record;

    @Before
    public void init() throws IOException {
        if(!new File("src/test/resources/database.arz").exists()) {
            throw new IOException("File src/test/resources/database.arz is missing," +
                    " copy the database to execute the tests");
        }
        Database database = new Database("src/test/resources/database.arz");
        skills = database.skills();

        for (Skill s : skills.getSkillList().values()) {
            assertNotNull(s);
            if (s.getClassName().equals(Database.Classes.SKILL_SPAWNPET)) {
                assertNotNull(s.getSpawnObjects());
                for (DbRecord o : s.getSpawnObjects()) {
                    if (o.getRecordType().equals(Database.Classes.PET)) {
                        record = o;
                        return;
                    }
                }
            }
        }
    }

    @Test
    public void getSpawnObject_Given_DbRecord_Then_returnPet() {
        Pet pet = (Pet) skills.getSpawnObject(record);
        assertNotNull(pet);
    }

    @Test
    public void getSpawnObject_Given_invalidRecord_Then_returnNull() {
        Pet pet = (Pet) skills.getSpawnObject(skills.getMasteries().get(0).getDbRecord());
        assertNull(pet);
    }

    @Test
    public void getSpawnObjectFromPath_Given_invalidRecord_Then_returnNull() {
        Pet pet = (Pet) skills.getSpawnObjectFromPath("RECORDS\\GAME\\GAMEENGINE.DBR");
        assertNull(pet);
    }

    @Test
    public void getSpawnObjectFromPath_Given_recordPath_Then_returnPet() {
        Pet pet = (Pet) skills.getSpawnObjectFromPath(record.getId());
        assertNotNull(pet);
    }

//    @Test
//    public void getVarTableString_Given_dbRecordPrefix_Then_returnVariableList() {
//        Hashtable<String, String> table = skills.getVarTableString(skills.getSkillTreeRecords().get(0),
//                Database.Variables.PREFIX_SKILL_NAME);
//        assertNotNull(table);
//        assertTrue(table.size() > 0);
//    }
//
//    @Test
//    public void getVarTableString_Given_invalidRecordPrefix_Then_returnNull() {
//        assertNull(skills.getVarTableString(new DbRecord(),
//                Database.Variables.PREFIX_SKILL_NAME));
//    }
}