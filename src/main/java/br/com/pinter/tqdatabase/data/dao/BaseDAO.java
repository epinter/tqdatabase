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

package br.com.pinter.tqdatabase.data.dao;

import br.com.pinter.tqdatabase.Database;
import br.com.pinter.tqdatabase.data.DatabaseReader;
import br.com.pinter.tqdatabase.models.BaseType;
import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.models.DbVariable;
import br.com.pinter.tqdatabase.models.Pet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
interface BaseDAO {
    DatabaseReader getDatabaseReader();

    default DbRecord getRecord(String recordPath) {
        return getDatabaseReader().getRecord(recordPath);
    }

    default boolean recordExists(String recordPath) {
        return getDatabaseReader().recordExists(recordPath);
    }

    default BaseType getSpawnObject(DbRecord rs) {
        if(rs == null) {
            return null;
        }
        String className = (String) rs.getFirstValue(Database.Variables.CLASS);
        String characterRacialProfile = (String) rs.getFirstValue(Database.Variables.CHARACTER_RACIAL_PROFILE);
        String actorName = (String) rs.getFirstValue(Database.Variables.ACTOR_NAME);
        String description = (String) rs.getFirstValue(Database.Variables.DESCRIPTION);
        List<Integer> charLevel = rs.getListInteger(Database.Variables.CHAR_LEVEL);
        if (className != null && className.equalsIgnoreCase(Database.Classes.PET)) {
            Pet p = new Pet();
            p.setRecordPath(rs.getId());
            p.setCharacterRacialProfile(characterRacialProfile);
            p.setCharLevel(charLevel);
            p.setClassName(className);
            p.setActorName(actorName);
            p.setDescription(description);
            p.setSkillNameTable(getVarTableString(rs, Database.Variables.PREFIX_SKILL_NAME));
            return p;
        }
        return null;
    }

    void preload();

    default BaseType getSpawnObjectFromPath(String recordPath) {
        return getSpawnObject(getRecord(recordPath));
    }

    @SuppressWarnings("SameParameterValue")
    default Map<String, String> getVarTableString(DbRecord r, String prefix) {
        if (r == null || r.getVariables().isEmpty()) {
            return null;
        }
        Map<String, String> table = new HashMap<>();
        for (String k : r.getVariables().keySet()) {
            if (k.matches(prefix + "\\d+") && r.getVariables().get(k).getType() == DbVariable.Type.STRING) {
                table.put(k, (String) r.getVariables().get(k).getValues().get(0));
            }
        }
        return table;
    }

    default Map<String, DbVariable> getVarTable(DbRecord r, String prefix) {
        if (r == null || r.getVariables().isEmpty()) {
            return null;
        }
        Map<String, DbVariable> table = new HashMap<>();
        for (String k : r.getVariables().keySet()) {
            if (k.matches(prefix + "\\d+")) {
                table.put(k, r.getVariables().get(k));
            }
        }
        return table;
    }

    default DbVariable getVariableFromVarTableIndex(Map<String, DbVariable> table, int index) {
        for (Map.Entry<String, DbVariable> k: table.entrySet()) {
            String strIdx = k.getKey().replaceAll("[a-zA-Z]+(\\d+)$", "$1");
            int idx;
            try {
                idx = Integer.parseInt(strIdx);
                if (index == idx) {
                    return k.getValue();
                }
            } catch (NumberFormatException ignored) {
                //ignored
            }
        }
        return null;
    }
}
