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

import java.util.List;
import java.util.Map;

public class Pc {
    private Float characterLife;
    private Float characterMana;
    private Float characterStrength;
    private Float characterIntelligence;
    private Float characterDexterity;
    private Map<String, String> skillTreeTable;
    private Gender gender = Gender.MALE;
    private List<String> playerTextures;

    public enum Gender {
        MALE,
        FEMALE
    }

    public Float getCharacterLife() {
        return characterLife;
    }

    public void setCharacterLife(Float characterLife) {
        this.characterLife = characterLife;
    }

    public Float getCharacterMana() {
        return characterMana;
    }

    public void setCharacterMana(Float characterMana) {
        this.characterMana = characterMana;
    }

    public Float getCharacterStrength() {
        return characterStrength;
    }

    public void setCharacterStrength(Float characterStrength) {
        this.characterStrength = characterStrength;
    }

    public Float getCharacterIntelligence() {
        return characterIntelligence;
    }

    public void setCharacterIntelligence(Float characterIntelligence) {
        this.characterIntelligence = characterIntelligence;
    }

    public Float getCharacterDexterity() {
        return characterDexterity;
    }

    public void setCharacterDexterity(Float characterDexterity) {
        this.characterDexterity = characterDexterity;
    }

    public Map<String, String> getSkillTreeTable() {
        return skillTreeTable;
    }

    public void setSkillTreeTable(Map<String, String> skillTreeTable) {
        this.skillTreeTable = skillTreeTable;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<String> getPlayerTextures() {
        return playerTextures;
    }

    public void setPlayerTextures(List<String> playerTextures) {
        this.playerTextures = playerTextures;
    }

    @Override
    public String toString() {
        return "Pc{" +
                "characterLife=" + characterLife +
                ", characterMana=" + characterMana +
                ", characterStrength=" + characterStrength +
                ", characterIntelligence=" + characterIntelligence +
                ", characterDexterity=" + characterDexterity +
                ", skillTreeTable=" + skillTreeTable +
                ", gender=" + gender +
                ", playerTextures=" + playerTextures +
                '}';
    }
}
