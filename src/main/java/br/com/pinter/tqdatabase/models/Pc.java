/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.models;

import java.util.HashMap;

public class Pc {
    private Float characterLife;
    private Float characterMana;
    private Float characterStrength;
    private Float characterIntelligence;
    private Float characterDexterity;
    private HashMap<String,String> skillTreeTable;

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

    public HashMap<String, String> getSkillTreeTable() {
        return skillTreeTable;
    }

    public void setSkillTreeTable(HashMap<String, String> skillTreeTable) {
        this.skillTreeTable = skillTreeTable;
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
                '}';
    }
}
