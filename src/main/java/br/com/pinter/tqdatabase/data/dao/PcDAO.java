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

import br.com.pinter.tqdatabase.data.DatabaseReader;
import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.models.DbVariable;
import br.com.pinter.tqdatabase.models.Pc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PcDAO implements BaseDAO {
    private static final System.Logger logger = System.getLogger(PcDAO.class.getName());
    private final DatabaseReader databaseReader;
    private Pc pc;

    @Override
    public DatabaseReader getDatabaseReader() {
        return databaseReader;
    }

    public PcDAO(DatabaseReader databaseReader) {
        this.databaseReader = databaseReader;
    }

    @Override
    public void preload() {
        pc = getPc();
    }

    public Pc getPc() {
        return getPc(Pc.Gender.MALE);
    }

    public Pc getPc(Pc.Gender gender) {
        if (pc != null && gender.equals(pc.getGender())) {
            return pc;
        }

        Pc p = new Pc();
        String recordGender = Constants.RECORD_PC_MALE;

        if (gender.equals(Pc.Gender.FEMALE)) {
            recordGender = Constants.RECORD_PC_FEMALE;
        }

        DbRecord r = getRecord(recordGender);

        if (r == null) {
            throw new IllegalStateException("player character record not found in database)");
        }
        List<DbVariable> skillTreeVars = DbVariable.filterRecordVariables(r, Constants.REGEXP_FIELD_SKILLTREE);
        Map<String, DbVariable> dbVariables = r.getVariables();

        if (dbVariables != null && !dbVariables.isEmpty()) {
            p.setCharacterLife((Float) dbVariables.get("characterLife").getFirstValue());
            p.setCharacterMana((Float) dbVariables.get("characterMana").getFirstValue());
            p.setCharacterStrength((Float) dbVariables.get("characterStrength").getFirstValue());
            p.setCharacterIntelligence((Float) dbVariables.get("characterIntelligence").getFirstValue());
            p.setCharacterDexterity((Float) dbVariables.get("characterDexterity").getFirstValue());
            p.setPlayerTextures(dbVariables.get("playerTextures").getListString());
            Map<String, String> list = new HashMap<>();

            if (skillTreeVars != null) {
                for (DbVariable v : skillTreeVars) {
                    list.put(v.getVariableName(), (String) v.getFirstValue());
                }
            }
            p.setSkillTreeTable(list);


            if (p.getCharacterDexterity() <= 0
                    || p.getCharacterIntelligence() <= 0
                    || p.getCharacterStrength() <= 0
                    || p.getCharacterLife() <= 0
                    || p.getCharacterMana() <= 0
                    || p.getSkillTreeTable() == null
                    || p.getSkillTreeTable().isEmpty()) {
                throw new IllegalStateException("illegal value reading database (pc)");
            }
        }
        logger.log(System.Logger.Level.DEBUG, "pc: found ''{0}''", p);
        return p;
    }
}

