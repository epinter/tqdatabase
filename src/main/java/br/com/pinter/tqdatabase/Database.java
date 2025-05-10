/*
 * Copyright (C) 2022 Emerson Pinter - All Rights Reserved
 */

/*    This file is part of TQ Database.

    TQ Database is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    TQ Database is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with TQ Database.  If not, see <http://www.gnu.org/licenses/>.
*/

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.data.DatabaseReader;
import br.com.pinter.tqdatabase.models.DbNode;
import br.com.pinter.tqdatabase.models.DbRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;

import static java.lang.System.Logger.Level.INFO;

/**
 * This class is designed to concentrate all access to data contained in the game database by using methods to
 * delegate to specific classes, like {@link Database#skills}
 */
public class Database {
    private static final System.Logger logger = System.getLogger(Database.class.getName());
    private final Skills skills;
    private final Player player;
    private final Teleports teleports;
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

    public Database(String[] fileNames, boolean useCache) throws IOException {
        databaseReader = new DatabaseReader(fileNames, useCache);
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

    public Player player() {
        return player;
    }

    public Teleports teleports() {
        return teleports;
    }

    public static String normalizeRecordPath(String recordId) {
        return DbRecord.normalizeRecordPath(recordId);
    }

    public List<Path> getLoadedDb() {
        return databaseReader.getLoadedDb();
    }

    public Set<DbRecord> getRecordsForDb(Path arzFilename) {
        return databaseReader.getRecordsForDb(arzFilename);
    }

    /**
     * Creates a tree of nodes representing the database, directory nodes have record field null, DBR records contain a DbRecord instance.
     *
     * @param db Path to an already loaded database
     * @return Tree
     */
    private DbNode getTree(Path db, boolean loadRecords) {
        Map<Path, DbNode> nodes = new HashMap<>();
        for (Path s : databaseReader.getLoadedDb()) {
            if (!s.equals(Path.of(db.toString()))) {
                continue;
            }
            for (var e : databaseReader.getRecordsForDb(s)) {
                Path path = Path.of(e.getId().replace("\\", Matcher.quoteReplacement(File.separator)));
                for (int i = 0; i < path.getNameCount(); i++) {
                    Path nodeName;
                    DbNode n;
                    if (i < path.getNameCount() - 1) {
                        nodeName = path.subpath(0, i + 1);
                        n = new DbNode(nodeName);
                    } else {
                        //DBR
                        nodeName = path;
                        if (loadRecords) {
                            n = new DbNode(nodeName, getRecord(nodeName.toString()));
                        } else {
                            n = new DbNode(nodeName);
                        }
                    }
                    if (!nodes.containsKey(n.getName())) {
                        nodes.put(n.getName(), n);
                    }
                    if (nodes.containsKey(n.getName().getParent()) && !nodes.get(n.getName().getParent()).getChildren().contains(n)) {
                        nodes.get(n.getName().getParent()).addChild(n);
                    }
                }
            }
        }
        DbNode root = new DbNode(Path.of(File.separator));
        nodes.values().stream().filter(f -> f.getName().getParent() == null).forEach(root::addChild);
        return root;
    }

    /**
     * Executes the function using the record from parameter.
     *
     * @param nodeRecord An object containing the tree
     * @param function   Function to execute for each record
     */
    public void processTree(DbNode nodeRecord, Function<DbNode, Void> function) {
        for (var n : nodeRecord.getChildren()) {
            if (n.getChildren().isEmpty()) {
                function.apply(n);
            } else {
                processTree(n, function);
            }
        }
    }

    /**
     * Creates a tree of nodes representing the database, directory nodes have record field null, DBR records contain a DbRecord instance.
     *
     * @param db Path to an already loaded database
     * @return Tree
     */
    public DbNode getTreeWithContent(Path db) {
        return getTree(db, true);
    }

    /**
     * Creates a tree of nodes representing the database. No records will be loaded.
     *
     * @param db Path to an already loaded database
     * @return Tree
     */
    public DbNode getTree(Path db) {
        return getTree(db, false);
    }

    /**
     * Executes the function for each record in the database. Records are NOT loaded.
     *
     * @param db       A path to an already loaded database
     * @param function Function to execute for each record
     */
    public void processTree(Path db, Function<DbNode, Void> function) {
        processTree(getTree(db, false), function);

    }

    /**
     * Executes the function for each record in the database. Records are loaded.
     *
     * @param db       A path to an already loaded database
     * @param function Function to execute for each record
     */
    public void processTreeWithContent(Path db, Function<DbNode, Void> function) {
        processTree(getTree(db, true), function);
    }

    public void loadMod(Path dbPath) throws IOException {
        logger.log(INFO, "Loading database from ''{0}''", dbPath);
        databaseReader.loadMod(dbPath);
        preloadAll();
    }

    public void unloadMods() {
        databaseReader.unloadMods();
        preloadAll();
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
