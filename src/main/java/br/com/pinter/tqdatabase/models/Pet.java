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
