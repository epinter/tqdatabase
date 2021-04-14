/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.models;

import java.util.Hashtable;
import java.util.List;

public class Pet extends BaseType {
    private String characterRacialProfile;
    private List<Integer> charLevel;
    private String description;
    private Hashtable<String, String> skillNameTable;

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

    public Hashtable<String, String> getSkillNameTable() {
        return skillNameTable;
    }

    public void setSkillNameTable(Hashtable<String, String> skillNameTable) {
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
