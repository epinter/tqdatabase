/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.models.DbVariable;
import br.com.pinter.tqdatabase.models.Skill;
import br.com.pinter.tqdatabase.util.Constants;
import br.com.pinter.tqdatabase.util.Util;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

class SkillDAO implements BaseDAO {
    private Map<String, Skill> skillList;
    private final DatabaseReader databaseReader;
    private final System.Logger logger = Util.getLogger(SkillDAO.class.getName());

    SkillDAO(DatabaseReader databaseReader) {
        this.databaseReader = databaseReader;
    }

    @Override
    public DatabaseReader getDatabaseReader() {
        return databaseReader;
    }

    @Override
    public void preload() {
        getSkillList();
    }

    public List<Skill> getMasteries() {
        return getSkillList().values().stream().filter(Skill::isMastery).collect(Collectors.toList());
    }

    public List<Skill> getSkillsFromMastery(Skill mastery) {
        return getSkillList().values().stream().filter(
                skill -> {
                    if (skill.getParentPath() != null) {
                        return skill.getParentPath().equals(mastery.getRecordPath());
                    }
                    return false;
                }
        ).collect(Collectors.toList());
    }

    public Skill getSkill(String recordPath, boolean resolve) {
        Instant i = Instant.now();
        Map<String, Skill> skills = getSkillList();
        logger.log(System.Logger.Level.TRACE, "Skill List generation took " + Duration.between(i, Instant.now()));
        Skill skill = skills.get(Util.normalizeRecordPath(recordPath));

        if (skill == null) {
            return null;
        }
        if (!resolve) {
            return skill;
        }

        if (skill.isPointsToBuff()) {
            return getSkill(skill.getBuffPath(), resolve);
        } else if (skill.isPointsToPet()) {
            return getSkill(skill.getPetPath(), resolve);
        } else {
            return skill;
        }
    }

    private Integer getSkillLevelFromName(DbVariable skillNameVar, Map<String, DbVariable> skillLevelVars) {
        String strIdx = skillNameVar.getVariableName().replaceAll("[a-zA-Z]+(\\d+)$", "$1");

        DbVariable skillLevelVar = getVariableFromVarTableIndex(skillLevelVars, Integer.parseInt(strIdx));
        if (skillLevelVar != null && skillLevelVar.getType() == DbVariable.Type.INTEGER) {
            return (Integer) skillLevelVar.getFirstValue();
        }

        return null;
    }

    private DbRecord getSkillFromName(DbVariable skillNameVar, DbRecord record) {
        if (skillNameVar.getType() == DbVariable.Type.STRING && skillNameVar.valuesCount() == 1) {
            String skillPath = skillNameVar.getFirstString();
            DbRecord r = getRecord(skillPath);
            if (r == null) {
                logger.log(System.Logger.Level.ERROR, "Skill not found (from skilltree): %s/%s",
                        skillPath, record.getId());
            }
            return r;
        }


        return null;
    }

    private List<DbVariable> getSkillNameVars(DbRecord record) {
        List<DbVariable> skillNameVars = Util.filterRecordVariables(record, Constants.REGEXP_FIELD_SKILLNAME);

        if (skillNameVars == null) {
            throw new IllegalStateException("Skill records not found, regexp failed: " + Constants.REGEXP_FIELD_SKILLNAME
                    + " for record " + record.getId());
        }

        return skillNameVars;
    }

    private Map<String, DbVariable> getSkillLevelVars(DbRecord record) {
        Map<String, DbVariable> skillLevelVars = getVarTable(record, Database.Variables.PREFIX_SKILL_LEVEL);

        if (skillLevelVars == null) {
            throw new IllegalStateException("Skill level variables not parsed for record " + record.getId());
        }

        return skillLevelVars;
    }

    private boolean isDbVarString(DbVariable v) {
        return v != null && v.getType() == DbVariable.Type.STRING && v.valuesCount() == 1;
    }

    public Map<String, Skill> getSkillList() {
        if (this.skillList != null) {
            return this.skillList;
        }
        Map<String, Skill> skills = new HashMap<>();

        List<DbRecord> skillTreeRecords = getSkillTreeRecords();

        if (skillTreeRecords == null) {
            return null;
        }

        skillTreeRecords.forEach(record -> {
            Objects.requireNonNull(record);

            List<DbVariable> skillNameVars = getSkillNameVars(record);
            Map<String, DbVariable> skillLevelVars = getSkillLevelVars(record);

            List<Skill> masterySkillList = new ArrayList<>();

            Skill currentMastery = null;
            for (DbVariable v : skillNameVars) {
                Integer skillLevel = getSkillLevelFromName(v, skillLevelVars);

                DbRecord rs = getSkillFromName(v, record);

                if ((skillLevel != null && skillLevel > 0) || rs == null
                        || (!isDbVarString(rs.getVariables().get(Database.Variables.CLASS)))) {
                    continue;
                }

                if (rs.getFirstValue(Database.Variables.CLASS).equals(Database.Classes.SKILL_MASTERY)) {
                    DbVariable skillDisplayName = rs.getVariables().get(Database.Variables.SKILL_DISPLAY_NAME);
                    Skill mastery;
                    mastery = getSkillFromRecord(rs);
                    mastery.setMastery(true);
                    mastery.setRecordPath(rs.getId());
                    if (skillDisplayName != null && skillDisplayName.hasValues()) {
                        mastery.setName(skillDisplayName.getFirstString());
                    }
                    mastery.setDbRecord(rs);
                    masterySkillList.add(mastery);
                    currentMastery = mastery;
                } else if (rs.getId().matches(Constants.REGEXP_PATH_SKILL)) {
                    Skill skill = getSkillFromRecord(rs);
                    masterySkillList.add(skill);
                    while (skill.isPointsToBuff() || skill.isPointsToPet()) {
                        if (skill.isPointsToPet()) {
                            DbRecord rsPet = getRecord(skill.getPetPath());
                            skill = getSkillFromRecord(rsPet);
                            skill.setDbRecord(rsPet);
                        } else if (skill.isPointsToBuff()) {
                            DbRecord rsBuff = getRecord(skill.getBuffPath());
                            skill = getSkillFromRecord(rsBuff);
                            skill.setDbRecord(rsBuff);
                        }
                        masterySkillList.add(skill);
                    }
                }
            }
            for (Skill skill : masterySkillList) {
                if (!skill.isMastery() && currentMastery != null) {
                    skill.setParentPath(currentMastery.getRecordPath());
                }
            }
            for (Skill s : masterySkillList) {
                skills.put(s.getRecordPath(), s);
            }
        });
        logger.log(System.Logger.Level.DEBUG, "skillList: found ''{0}''", skills.values().size());
        logger.log(System.Logger.Level.TRACE, "skillList: ''{0}''", skills.keySet());
        this.skillList = skills;
        return skills;
    }

    private Skill getSkillFromRecord(DbRecord rs) {
        if (rs == null) {
            return null;
        }
        String className = (String) rs.getFirstValue(Database.Variables.CLASS);
        DbVariable skillDisplayName = rs.getVariables().get(Database.Variables.SKILL_DISPLAY_NAME);
        DbVariable buffSkillName = rs.getVariables().get(Database.Variables.BUFF_SKILL_NAME);
        DbVariable petSkillName = rs.getVariables().get(Database.Variables.PET_SKILL_NAME);

        Skill s = new Skill();
        s.setClassName(className);
        if (buffSkillName != null && buffSkillName.hasValues() && skillDisplayName == null) {
            s.setBuffPath(Util.normalizeRecordPath(buffSkillName.getFirstString()));
            s.setPointsToBuff(true);
        } else if (petSkillName != null && petSkillName.hasValues() && skillDisplayName == null) {
            s.setPetPath(Util.normalizeRecordPath(petSkillName.getFirstString()));
            s.setPointsToPet(true);
        }

        DbVariable skillTier = rs.getVariables().get(Database.Variables.SKILL_TIER);
        DbVariable skillMaxlevel = rs.getVariables().get(Database.Variables.SKILL_MAX_LEVEL);
        DbVariable skillMasteryLevelRequired = rs.getVariables().get(Database.Variables.SKILL_MASTERY_LEVEL_REQUIRED);
        DbVariable skillUltimateLevel = rs.getVariables().get(Database.Variables.SKILL_ULTIMATE_LEVEL);
        DbVariable skillDependancy = rs.getVariables().get(Database.Variables.SKILL_DEPENDANCY);
        DbVariable spawnObjects = rs.getVariables().get(Database.Variables.SPAWN_OBJECTS);

        s.setRecordPath(rs.getId());
        if (skillDisplayName != null) {
            s.setName(skillDisplayName.getFirstString());
            s.setSkillDisplayName((skillDisplayName.getFirstString()));
        }
        if (skillTier != null && skillTier.hasValues()) {
            s.setSkillTier((Integer) skillTier.getFirstValue());
        }
        if (skillDependancy != null && skillDependancy.hasValues()) {
            final List<DbRecord> list = new ArrayList<>();
            skillDependancy.getValues().forEach(f -> {
                DbRecord sdRecord = getRecord((String) f);
                if (sdRecord != null) {
                    list.add(sdRecord);
                }
            });
            s.setSkillDependancy(list);
        }
        if (spawnObjects != null && spawnObjects.hasValues()) {
            final List<DbRecord> list = new ArrayList<>();
            spawnObjects.getValues().forEach(f -> {
                DbRecord soRecord = getRecord((String) f);
                if (soRecord != null) {
                    list.add(soRecord);
                }
            });
            s.setSpawnObjects(list);
        }
        if (skillMaxlevel != null && skillMaxlevel.hasValues()) {
            s.setSkillMaxLevel((Integer) skillMaxlevel.getFirstValue());
        }
        if (skillUltimateLevel != null && skillUltimateLevel.hasValues()) {
            s.setSkillUltimateLevel((Integer) skillUltimateLevel.getFirstValue());
        }
        if (skillMasteryLevelRequired != null && skillMasteryLevelRequired.hasValues()) {
            s.setSkillMasteryLevelRequired((Integer) skillMasteryLevelRequired.getFirstValue());
        }
        logger.log(System.Logger.Level.TRACE, "skillFromRecord: found ''{0}'': ''{1}''", s.getRecordPath(), s);
        return s;
    }

    private DbRecord getRecordPc() {
        String path;
        if (recordExists(Constants.RECORD_PC_MALE)) {
            path = Constants.RECORD_PC_MALE;
        } else {
            path = Constants.RECORD_PC_LEGACY;
        }
        return getRecord(path);
    }

    public List<DbRecord> getSkillTreeRecords() {
        LinkedHashMap<String, DbRecord> skills = new LinkedHashMap<>();

        List<DbVariable> varList = Util.filterRecordVariables(getRecordPc(),
                Constants.REGEXP_FIELD_SKILLTREE);

        if (varList == null) {
            return null;
        }
        for (DbVariable v : varList) {
            if (v.getType() == DbVariable.Type.STRING) {
                String p = (String) v.getFirstValue();
                if (p != null && !p.isEmpty() && !skills.containsKey(p) && p.matches(Constants.REGEXP_PATH_SKILLTREE)) {
                    String recordPath = Util.normalizeRecordPath(p);
                    DbRecord record = getRecord(recordPath);
                    if (record != null) {
                        skills.put(recordPath, record);
                    }
                }
            }
        }

        logger.log(System.Logger.Level.DEBUG, "skillTreeRecords: found ''{0}''", skills.values().size());
        logger.log(System.Logger.Level.TRACE, "skillTreeRecords: ''{0}''", skills.keySet());

        return new ArrayList<>(skills.values());
    }

}
