/*
 * Copyright (C) 2022 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabasetest;

import br.com.pinter.tqdatabase.Database;
import br.com.pinter.tqdatabase.Skills;
import br.com.pinter.tqdatabase.cache.CacheDbRecord;
import br.com.pinter.tqdatabase.models.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SkillDAOTest {
    private Skills skills;

    @BeforeEach
    void init() throws IOException {
        CacheDbRecord.getInstance().clear();
        if(!new File("src/test/resources/database.arz").exists()) {
            throw new IOException("File src/test/resources/database.arz is missing," +
                    " copy the database to execute the tests");
        }
        Database database = new Database(new String[]{"src/test/resources/database.arz"});
        skills = database.skills();
    }


    @Test
    void getMasteries_Should_returnListOfMasteries() {
        assertNotNull(skills.getMasteries());
        assertTrue(skills.getMasteries().size() > 0);
        assertTrue(skills.getMasteries().get(0).isMastery());
    }

    @Test
    void getSkill_Should_returnOneSkill() {
        assertNotNull(skills.getSkillList());
        Optional<Skill> first = skills.getSkillList().values().stream().findFirst();
        assertTrue(first.isPresent());
        Skill s = first.get();
        assertNotNull(s);
        skills.getSkill(s.getRecordPath(),true);
    }

    @Test
    void getSkillList_Should_returnListOfSkills() {
        assertNotNull(skills.getSkillList());
        assertTrue(skills.getSkillList().size() > 0);
    }

    @Test
    void getSkillsFromMastery_Given_mastery_Then_returnSkillsFromMastery() {
        for (Skill mastery:skills.getMasteries()) {
            List<Skill> list =  skills.getSkillsFromMastery(mastery);
            assertNotNull(list);
            assertFalse(list.isEmpty());
            for (Skill s:list) {
                assertEquals(s.getParentPath(), mastery.getRecordPath());
            }
        }
    }
}