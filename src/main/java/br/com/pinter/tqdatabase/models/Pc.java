/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.models;

import java.util.HashMap;
import java.util.List;

public class Pc {
    private Float characterLife;
    private Float characterMana;
    private Float characterStrength;
    private Float characterIntelligence;
    private Float characterDexterity;
    private HashMap<String,String> skillTreeTable;
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

    public HashMap<String, String> getSkillTreeTable() {
        return skillTreeTable;
    }

    public void setSkillTreeTable(HashMap<String, String> skillTreeTable) {
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
