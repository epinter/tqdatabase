/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.models;

import br.com.pinter.tqdatabase.util.Util;

import java.util.List;

public class Skill extends BaseType {
    private String name;
    private String parentPath;
    private boolean mastery = false;
    private boolean pointsToBuff = false;
    private boolean pointsToPet = false;
    private String buffPath;
    private String petPath;
    private int skillMaxLevel;
    private int skillUltimateLevel;
    private int skillMasteryLevelRequired;
    private String skillDisplayName;
    private String skillBaseDescription;
    private int skillTier;
    private String masteryEnumeration;
    private List<DbRecord> spawnObjects;
    private List<DbRecord> skillDependancy;
    private List<Integer> petBurstSpawn;
    private List<Integer> petLimit;
    private Integer spawnObjectsTimeToLive;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = Util.normalizeRecordPath(parentPath);
    }

    public boolean isMastery() {
        return mastery;
    }

    public void setMastery(boolean mastery) {
        this.mastery = mastery;
    }

    public boolean isPointsToBuff() {
        return pointsToBuff;
    }

    public void setPointsToBuff(boolean pointsToBuff) {
        this.pointsToBuff = pointsToBuff;
    }

    public boolean isPointsToPet() {
        return pointsToPet;
    }

    public void setPointsToPet(boolean pointsToPet) {
        this.pointsToPet = pointsToPet;
    }

    public String getBuffPath() {
        return buffPath;
    }

    public void setBuffPath(String buffPath) {
        this.buffPath = buffPath;
    }

    public String getPetPath() {
        return petPath;
    }

    public void setPetPath(String petPath) {
        this.petPath = petPath;
    }

    public int getSkillMaxLevel() {
        return skillMaxLevel;
    }

    public void setSkillMaxLevel(int skillMaxLevel) {
        this.skillMaxLevel = skillMaxLevel;
    }

    public int getSkillUltimateLevel() {
        return skillUltimateLevel;
    }

    public void setSkillUltimateLevel(int skillUltimateLevel) {
        this.skillUltimateLevel = skillUltimateLevel;
    }

    public int getSkillMasteryLevelRequired() {
        return skillMasteryLevelRequired;
    }

    public void setSkillMasteryLevelRequired(int skillMasteryLevelRequired) {
        this.skillMasteryLevelRequired = skillMasteryLevelRequired;
    }

    public String getSkillDisplayName() {
        return skillDisplayName;
    }

    public void setSkillDisplayName(String skillDisplayName) {
        this.skillDisplayName = skillDisplayName;
    }

    public String getSkillBaseDescription() {
        return skillBaseDescription;
    }

    public void setSkillBaseDescription(String skillBaseDescription) {
        this.skillBaseDescription = skillBaseDescription;
    }

    public int getSkillTier() {
        return skillTier;
    }

    public void setSkillTier(int skillTier) {
        this.skillTier = skillTier;
    }

    public String getMasteryEnumeration() {
        return masteryEnumeration;
    }

    public void setMasteryEnumeration(String masteryEnumeration) {
        this.masteryEnumeration = masteryEnumeration;
    }

    public List<DbRecord> getSpawnObjects() {
        return spawnObjects;
    }

    public void setSpawnObjects(List<DbRecord> spawnObjects) {
        this.spawnObjects = spawnObjects;
    }

    public List<DbRecord> getSkillDependancy() {
        return skillDependancy;
    }

    public void setSkillDependancy(List<DbRecord> skillDependancy) {
        this.skillDependancy = skillDependancy;
    }

    public List<Integer> getPetBurstSpawn() {
        return petBurstSpawn;
    }

    public void setPetBurstSpawn(List<Integer> petBurstSpawn) {
        this.petBurstSpawn = petBurstSpawn;
    }

    public List<Integer> getPetLimit() {
        return petLimit;
    }

    public void setPetLimit(List<Integer> petLimit) {
        this.petLimit = petLimit;
    }

    public Integer getSpawnObjectsTimeToLive() {
        return spawnObjectsTimeToLive;
    }

    public void setSpawnObjectsTimeToLive(Integer spawnObjectsTimeToLive) {
        this.spawnObjectsTimeToLive = spawnObjectsTimeToLive;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "name='" + name + '\'' +
                ", parentPath='" + parentPath + '\'' +
                ", mastery=" + mastery +
                ", pointsToBuff=" + pointsToBuff +
                ", pointsToPet=" + pointsToPet +
                ", buffPath='" + buffPath + '\'' +
                ", petPath='" + petPath + '\'' +
                ", skillMaxLevel=" + skillMaxLevel +
                ", skillUltimateLevel=" + skillUltimateLevel +
                ", skillMasteryLevelRequired=" + skillMasteryLevelRequired +
                ", skillDisplayName='" + skillDisplayName + '\'' +
                ", skillBaseDescription='" + skillBaseDescription + '\'' +
                ", skillTier=" + skillTier +
                ", masteryEnumeration='" + masteryEnumeration + '\'' +
                ", spawnObjects=" + spawnObjects +
                ", skillDependancy=" + skillDependancy +
                ", petBurstSpawn=" + petBurstSpawn +
                ", petLimit=" + petLimit +
                ", spawnObjectsTimeToLive=" + spawnObjectsTimeToLive +
                "} " + super.toString();
    }
}
