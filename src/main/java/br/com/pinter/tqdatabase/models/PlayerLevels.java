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
