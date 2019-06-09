/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.Database;
import br.com.pinter.tqdatabase.Skills;
import br.com.pinter.tqdatabase.models.Skill;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class SkillDAOTest {
    private Skills skills;

    @Before
    public void init() throws IOException {
        if(!new File("src/test/resources/database.arz").exists()) {
            throw new IOException("File src/test/resources/database.arz is missing," +
                    " copy the database to execute the tests");
        }
        Database database = new Database("src/test/resources/database.arz");
        skills = database.skills();
    }


    @Test
    public void getMasteries_Should_returnListOfMasteries() {
        assertNotNull(skills.getMasteries());
        assertTrue(skills.getMasteries().size() > 0);
        assertTrue(skills.getMasteries().get(0).isMastery());
    }

    @Test
    public void getSkill_Should_returnOneSkill() {
        assertNotNull(skills.getSkillList());
        Optional<Skill> first = skills.getSkillList().values().stream().findFirst();
        assertTrue(first.isPresent());
        Skill s = first.get();
        assertNotNull(s);
        skills.getSkill(s.getRecordPath(),true);
    }

    @Test
    public void getSkillList_Should_returnListOfSkills() {
        assertNotNull(skills.getSkillList());
        assertTrue(skills.getSkillList().size() > 0);
    }

    @Test
    public void getSkillsFromMastery_Given_mastery_Then_returnSkillsFromMastery() {
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