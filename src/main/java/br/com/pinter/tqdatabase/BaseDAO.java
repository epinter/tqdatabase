/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.models.BaseType;
import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.models.DbVariable;
import br.com.pinter.tqdatabase.models.Pet;

import java.util.Hashtable;
import java.util.List;

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
    default Hashtable<String, String> getVarTableString(DbRecord r, String prefix) {
        if (r == null || r.getVariables().isEmpty()) {
            return null;
        }
        Hashtable<String, String> table = new Hashtable<>();
        for (String k : r.getVariables().keySet()) {
            if (k.matches(prefix + "\\d+")) {
                if (r.getVariables().get(k).getType() == DbVariable.Type.String) {
                    table.put(k, (String) r.getVariables().get(k).getValues().get(0));
                }
            }
        }
        return table;
    }

    default Hashtable<String, DbVariable> getVarTable(DbRecord r, String prefix) {
        if (r == null || r.getVariables().isEmpty()) {
            return null;
        }
        Hashtable<String, DbVariable> table = new Hashtable<>();
        for (String k : r.getVariables().keySet()) {
            if (k.matches(prefix + "\\d+")) {
                table.put(k, r.getVariables().get(k));
            }
        }
        return table;
    }

    default DbVariable getVariableFromVarTableIndex(Hashtable<String, DbVariable> table, int index) {
        for (String k : table.keySet()) {
            String strIdx = k.replaceAll("[a-zA-Z]+(\\d+)$", "$1");
            int idx;
            try {
                idx = Integer.parseInt(strIdx);
                if (index == idx) {
                    return table.get(k);
                }
            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }
}
