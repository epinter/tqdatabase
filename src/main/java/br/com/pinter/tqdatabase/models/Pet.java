/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.models;

import java.util.List;
import java.util.Map;

public class Pet extends BaseType {
    private String characterRacialProfile;
    private List<Integer> charLevel;
    private String description;
    private Map<String, String> skillNameTable;

    public String getCharacterRacialProfile() {
        return characterRacialProfile;
    }

    public void setCharacterRacialProfile(String characterRacialProfile) {
        this.characterRacialProfile = characterRacialProfile;
    }

    public List<Integer> getCharLevel() {
        return charLevel;
    }

    public void setCharLevel(List<Integer> charLevel) {
        this.charLevel = charLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getSkillNameTable() {
        return skillNameTable;
    }

    public void setSkillNameTable(Map<String, String> skillNameTable) {
        this.skillNameTable = skillNameTable;
    }

    @Override
    public String toString() {
        return "Pet{" +
                "characterRacialProfile='" + characterRacialProfile + '\'' +
                ", charLevel=" + charLevel +
                ", description='" + description + '\'' +
                ", skillNameTable=" + skillNameTable +
                "} " + super.toString();
    }
}
