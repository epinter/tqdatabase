/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.util.Constants;
import br.com.pinter.tqdatabase.util.Util;

import java.io.IOException;

/**
 * This class is designed to concentrate all access to data contained in the game database by using methods to
 * delegate to specific classes, like {@link Database#skills}
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Database {
    private Skills skills;
    private Player player;
    private Teleports teleports;
    private String fileName;
    private ArzFile arzFile;

    /**
     * @param fileName The absolute path of the database.arz file.
     * @throws IOException if the file can't be read or parsed.
     */
    public Database(String fileName) throws IOException {
        arzFile = new ArzFile(fileName);
        skills = new Skills(arzFile);
        player = new Player(arzFile);
        teleports = new Teleports(arzFile);
    }

    /**
     * Preloads all data, from all delegates
     */
    public void preloadAll() {
        skills.preload();
        player.preload();
        teleports.preload();
    }

    /**
     * Returns a specific {@link DbRecord}
     *
     * @param recordPath The path to read the record from.
     * @return A {@link DbRecord} instance
     */
    public DbRecord getRecord(String recordPath) {
        return arzFile.getRecord(recordPath);
    }

    /**
     * Remove <b><code>recordId</code></b> from cache
     *
     * @param recordId The record-path to remove from cache
     */
    public void invalidateCacheEntry(String recordId) {
        arzFile.invalidateCacheEntry(recordId);
    }

    /**
     * Test if the record-path exists
     *
     * @param recordId The record to test
     * @return <b><code>true</code></b> if exists, <b><code>false</code></b> if not
     */
    public boolean recordExists(String recordId) {
        return arzFile.exists(recordId);
    }

    /**
     * Delegate to {@link Skills}
     *
     * @return {@link Skills}
     */
    public Skills skills() {
        return skills;
    }

    public Player player() { return player; }

    public Teleports teleports() { return teleports; }

    public static String normalizeRecordPath(String recordId) {
        return Util.normalizeRecordPath(recordId);
    }

    public static class Classes {
        public static final String SKILL_SPAWNPET = "Skill_SpawnPet";
        public static final String PET = "Pet";
        public static final String SKILL_MASTERY = "Skill_Mastery";
    }

    public static class Variables {
        public static final String CLASS = "Class";
        public static final String CHARACTER_RACIAL_PROFILE = "characterRacialProfile";
        public static final String ACTOR_NAME = "ActorName";
        public static final String DESCRIPTION = "description";
        public static final String CHAR_LEVEL = "charLevel";
        public static final String SKILL_DISPLAY_NAME = "skillDisplayName";
        public static final String BUFF_SKILL_NAME = "buffSkillName";
        public static final String PET_SKILL_NAME = "petSkillName";
        public static final String SKILL_TIER = "skillTier";
        public static final String SKILL_MAX_LEVEL = "skillMaxLevel";
        public static final String SKILL_MASTERY_LEVEL_REQUIRED = "skillMasteryLevelRequired";
        public static final String SKILL_ULTIMATE_LEVEL = "skillUltimateLevel";
        public static final String SKILL_DEPENDANCY = "skillDependancy";
        public static final String SPAWN_OBJECTS = "spawnObjects";
        public static final String PREFIX_SKILL_NAME = "skillName";
        public static final String PREFIX_SKILL_LEVEL = "skillLevel";
        public static final String SKILL_NAME = "skillName";
        public static final String LEVEL_FILE_NAME = "levelFileName";
    }
}
