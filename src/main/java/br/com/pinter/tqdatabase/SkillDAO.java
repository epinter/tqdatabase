/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
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
        Map<String, Skill> skillList = getSkillList();
        logger.log(System.Logger.Level.TRACE, "Skill List generation took " + Duration.between(i, Instant.now()));
        Skill skill = skillList.get(Util.normalizeRecordPath(recordPath));

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

    public Map<String, Skill> getSkillList() {
        if (this.skillList != null) {
            return this.skillList;
        }
        Hashtable<String, Skill> skillList = new Hashtable<>();

        List<DbRecord> skillTreeRecords = getSkillTreeRecords();

        if (skillTreeRecords == null) {
            return null;
        }

        skillTreeRecords.forEach(f -> {
            DbRecord r = getRecord(f.getId());
            if (r == null) {
                throw new RuntimeException("Error parsing record (skillTree)" + f.getId());
            }

            DbRecord skillNameVarsRecord = getRecord(r.getId());
            if (skillNameVarsRecord == null) {
                throw new RuntimeException("Error parsing record (skillName)" + f.getId());
            }

            List<DbVariable> skillNameVars = Util.filterRecordVariables(skillNameVarsRecord, Constants.REGEXP_FIELD_SKILLNAME);
            Hashtable<String, DbVariable> skillLevelVars = getVarTable(r, Database.Variables.PREFIX_SKILL_LEVEL);

            if (skillNameVars == null) {
                throw new RuntimeException("Skill records not found, regexp failed: " + Constants.REGEXP_FIELD_SKILLNAME
                        + " for record " + f.getId());
            }

            if (skillLevelVars == null) {
                throw new RuntimeException("Skill level variables not parsed for record " + f.getId());
            }

            List<Skill> masterySkillList = new ArrayList<>();

            Skill currentMastery = null;
            for (DbVariable v : skillNameVars) {
                String strIdx = v.getVariableName().replaceAll("[a-zA-Z]+(\\d+)$", "$1");

                DbVariable skillLevelVar = getVariableFromVarTableIndex(skillLevelVars, Integer.parseInt(strIdx));
                Integer skillLevelValue = null;
                if (skillLevelVar != null) {
                    skillLevelValue = (Integer) skillLevelVar.getFirstValue();
                }
                if (skillLevelValue != null && skillLevelVar.getType() == DbVariable.Type.Integer && skillLevelValue > 0) {
                    continue;
                }

                if (v.getType() == DbVariable.Type.String && v.getValues() != null && v.getValues().size() == 1) {
                    String skillPath = (String) v.getValues().get(0);
                    DbRecord rs = getRecord(skillPath);
                    if (rs == null) {
                        logger.log(System.Logger.Level.ERROR, "Skill not found (from skilltree): %s/%s",
                                skillPath, f.getId());
                        continue;
                    }
                    DbVariable varClass = rs.getVariables().get(Database.Variables.CLASS);

                    if (varClass != null && varClass.getType() == DbVariable.Type.String && varClass.getValues().size() == 1) {
                        if (varClass.getValues().get(0).equals(Database.Classes.SKILL_MASTERY)) {
                            DbVariable skillDisplayName = rs.getVariables().get(Database.Variables.SKILL_DISPLAY_NAME);
                            Skill mastery;
                            mastery = getSkillFromRecord(rs);
                            mastery.setMastery(true);
                            mastery.setRecordPath(rs.getId());
                            if (skillDisplayName != null && skillDisplayName.getValues() != null) {
                                mastery.setName((String) skillDisplayName.getValues().get(0));
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
                }
            }
            for (Skill skill : masterySkillList) {
                if (!skill.isMastery() && currentMastery != null) {
                    skill.setParentPath(currentMastery.getRecordPath());
                }
            }
            for (Skill s : masterySkillList) {
                skillList.put(s.getRecordPath(), s);
            }
        });
        logger.log(System.Logger.Level.DEBUG, "skillList: found ''{0}''", skillList.values().size());
        logger.log(System.Logger.Level.TRACE, "skillList: ''{0}''", skillList.keySet());
        this.skillList = skillList;
        return skillList;
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
        if (buffSkillName != null && buffSkillName.getValues() != null && skillDisplayName == null) {
            s.setBuffPath(Util.normalizeRecordPath((String) buffSkillName.getValues().get(0)));
            s.setPointsToBuff(true);
        } else if (petSkillName != null && petSkillName.getValues() != null && skillDisplayName == null) {
            s.setPetPath(Util.normalizeRecordPath((String) petSkillName.getValues().get(0)));
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
            s.setName((String) skillDisplayName.getValues().get(0));
            s.setSkillDisplayName(((String) skillDisplayName.getValues().get(0)));
        }
        if (skillTier != null && skillTier.getValues() != null) {
            s.setSkillTier((Integer) skillTier.getValues().get(0));
        }
        if (skillDependancy != null && skillDependancy.getValues() != null) {
            final List<DbRecord> list = new ArrayList<>();
            skillDependancy.getValues().forEach(f -> {
                DbRecord sdRecord = getRecord((String) f);
                if (sdRecord != null) {
                    list.add(sdRecord);
                }
            });
            s.setSkillDependancy(list);
        }
        if (spawnObjects != null && spawnObjects.getValues() != null) {
            final List<DbRecord> list = new ArrayList<>();
            spawnObjects.getValues().forEach(f -> {
                DbRecord soRecord = getRecord((String) f);
                if (soRecord != null) {
                    list.add(soRecord);
                }
            });
            s.setSpawnObjects(list);
        }
        if (skillMaxlevel != null && skillMaxlevel.getValues() != null) {
            s.setSkillMaxLevel((Integer) skillMaxlevel.getValues().get(0));
        }
        if (skillUltimateLevel != null && skillUltimateLevel.getValues() != null) {
            s.setSkillUltimateLevel((Integer) skillUltimateLevel.getValues().get(0));
        }
        if (skillMasteryLevelRequired != null && skillMasteryLevelRequired.getValues() != null) {
            s.setSkillMasteryLevelRequired((Integer) skillMasteryLevelRequired.getValues().get(0));
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
            if (v.getType() == DbVariable.Type.String) {
                String p = (String) v.getValues().get(0);
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
