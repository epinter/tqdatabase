/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.util.Util;

import java.io.IOException;

/**
 * This class is designed to concentrate all access to data contained in the game database by using methods to
 * delegate to specific classes, like {@link Database#skills}
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Database {
    private final Skills skills;
    private final Player player;
    private final Teleports teleports;
    private String fileName;
    private final DatabaseReader databaseReader;

    /**
     * @param fileNames An array with the absolute path of database.arz files to be loaded. The databases will be loaded using
     *                  the order specified, and the records will be overriden by the database loaded last.
     * @throws IOException if the file can't be read or parsed.
     */
    public Database(String[] fileNames) throws IOException {
        databaseReader = new DatabaseReader(fileNames);
        skills = new Skills(databaseReader);
        player = new Player(databaseReader);
        teleports = new Teleports(databaseReader);
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
     * @see DatabaseReader#getRecord(String)
     */
    public DbRecord getRecord(String recordPath) {
        return databaseReader.getRecord(recordPath);
    }

    /**
     * @see DatabaseReader#invalidateCacheEntry(String)
     */
    public void invalidateCacheEntry(String recordId) {
        databaseReader.invalidateCacheEntry(recordId);
    }

    /**
     * @see DatabaseReader#recordExists(String)
     */
    public boolean recordExists(String recordId) {
        return databaseReader.recordExists(recordId);
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
        private Classes() {
        }

        public static final String SKILL_SPAWNPET = "Skill_SpawnPet";
        public static final String PET = "Pet";
        public static final String SKILL_MASTERY = "Skill_Mastery";
    }

    public static class Variables {
        private Variables() {
        }

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
