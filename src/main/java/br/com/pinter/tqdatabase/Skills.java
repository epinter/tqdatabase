/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.models.BaseType;
import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.models.Skill;

import java.util.List;
import java.util.Map;

/**
 * Class to work with Skills from game database
 */
public class Skills implements TQService {
    private SkillDAO skillDAO;

    /**
     * Constructor, uses a propertly initialized {@link DatabaseReader}. Designed to be initialized by {@link Database} class.
     *
     * @param databaseReader An instance of {@link DatabaseReader}
     */
    Skills(DatabaseReader databaseReader) {
        skillDAO = new SkillDAO(databaseReader);
    }

    /**
     * Preloads skills related data from database.
     */
    public void preload() {
        skillDAO.preload();
    }

    /**
     * Search for all masteries available in game.
     *
     * @return List of {@link Skill}, each item representing a mastery.
     */
    public List<Skill> getMasteries() {
        return skillDAO.getMasteries();
    }

    /**
     * Search all skills contained in the given <b><code>mastery</code></b>
     *
     * @param mastery The mastery to get the skills from.
     * @return A {@link List} of {@link Skill} from the <b><code>mastery</code></b>
     */
    public List<Skill> getSkillsFromMastery(Skill mastery) {
        return skillDAO.getSkillsFromMastery(mastery);
    }

    /**
     * Get the skill located at <b><code>recordPath</code></b>
     *
     * @param recordPath Path of the skill record
     * @param resolve If <b>true</b>, buff skills and pets will be resolved (followed). The target record specified in
     *                the buff or pet variable in the <b><code>recordPath</code></b> will be used to find and return the final record.
     *                If <b>false</b>, the skill record specified by <b><code>recordPath</code></b> will be returned.
     * @return The {@link Skill} object representing <b><code>recordPath</code></b>
     */
    public Skill getSkill(String recordPath, boolean resolve) {
        return skillDAO.getSkill(recordPath, resolve);
    }

    /**
     * Get a list of all Skills available in game
     *
     * @return A map where the key is the record-path of {@link Skill}, the value is a {@link Skill}.
     */
    public Map<String, Skill> getSkillList() {
        return skillDAO.getSkillList();
    }

    /**
     * Build a spawn-object from passed record. Currently only {@link br.com.pinter.tqdatabase.models.Pet} is supported.
     *
     * @param rs A {@link DbRecord} to build a SpawnObject from.
     * @return A SpawnObject object type.
     */
    public BaseType getSpawnObject(DbRecord rs) {
        return skillDAO.getSpawnObject(rs);
    }


    /**
     * Build a spawn-object from passed record-path. Currently only {@link br.com.pinter.tqdatabase.models.Pet} is supported.
     *
     * @param recordPath A record-path to build a SpawnObject from.
     * @return A SpawnObject object type.
     */
    public BaseType getSpawnObjectFromPath(String recordPath) {
        return skillDAO.getSpawnObjectFromPath(recordPath);
    }
}
