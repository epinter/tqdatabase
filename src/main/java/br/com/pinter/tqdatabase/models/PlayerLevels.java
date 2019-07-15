/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.models;

public class PlayerLevels {
    private Integer skillModifierPoints;
    private Integer manaIncrement;
    private Integer maxPlayerLevel;
    private Integer intelligenceIncrement;
    private Integer initialSkillPoints;
    private Integer lifeIncrement;
    private Integer dexterityIncrement;
    private String experienceLevelEquation;
    private Integer strengthIncrement;
    private Integer characterModifierPoints;

    public Integer getSkillModifierPoints() {
        return skillModifierPoints;
    }

    public void setSkillModifierPoints(Integer skillModifierPoints) {
        this.skillModifierPoints = skillModifierPoints;
    }

    public Integer getManaIncrement() {
        return manaIncrement;
    }

    public void setManaIncrement(Integer manaIncrement) {
        this.manaIncrement = manaIncrement;
    }

    public Integer getMaxPlayerLevel() {
        return maxPlayerLevel;
    }

    public void setMaxPlayerLevel(Integer maxPlayerLevel) {
        this.maxPlayerLevel = maxPlayerLevel;
    }

    public Integer getIntelligenceIncrement() {
        return intelligenceIncrement;
    }

    public void setIntelligenceIncrement(Integer intelligenceIncrement) {
        this.intelligenceIncrement = intelligenceIncrement;
    }

    public Integer getInitialSkillPoints() {
        return initialSkillPoints;
    }

    public void setInitialSkillPoints(Integer initialSkillPoints) {
        this.initialSkillPoints = initialSkillPoints;
    }

    public Integer getLifeIncrement() {
        return lifeIncrement;
    }

    public void setLifeIncrement(Integer lifeIncrement) {
        this.lifeIncrement = lifeIncrement;
    }

    public Integer getDexterityIncrement() {
        return dexterityIncrement;
    }

    public void setDexterityIncrement(Integer dexterityIncrement) {
        this.dexterityIncrement = dexterityIncrement;
    }

    public String getExperienceLevelEquation() {
        return experienceLevelEquation;
    }

    public void setExperienceLevelEquation(String experienceLevelEquation) {
        this.experienceLevelEquation = experienceLevelEquation;
    }

    public Integer getStrengthIncrement() {
        return strengthIncrement;
    }

    public void setStrengthIncrement(Integer strengthIncrement) {
        this.strengthIncrement = strengthIncrement;
    }

    public Integer getCharacterModifierPoints() {
        return characterModifierPoints;
    }

    public void setCharacterModifierPoints(Integer characterModifierPoints) {
        this.characterModifierPoints = characterModifierPoints;
    }

    @Override
    public String toString() {
        return "PlayerLevels{" +
                "skillModifierPoints=" + skillModifierPoints +
                ", manaIncrement=" + manaIncrement +
                ", maxPlayerLevel=" + maxPlayerLevel +
                ", intelligenceIncrement=" + intelligenceIncrement +
                ", initialSkillPoints=" + initialSkillPoints +
                ", lifeIncrement=" + lifeIncrement +
                ", dexterityIncrement=" + dexterityIncrement +
                ", experienceLevelEquation='" + experienceLevelEquation + '\'' +
                ", strengthIncrement=" + strengthIncrement +
                ", characterModifierPoints=" + characterModifierPoints +
                '}';
    }
}
