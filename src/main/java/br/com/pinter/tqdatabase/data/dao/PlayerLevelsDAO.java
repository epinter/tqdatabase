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
import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.models.DbVariable;
import br.com.pinter.tqdatabase.models.PlayerLevels;
import br.com.pinter.tqdatabase.util.Constants;
import br.com.pinter.tqdatabase.util.Util;

import java.util.List;
import java.util.Map;

public class PlayerLevelsDAO implements BaseDAO {
    private final DatabaseReader databaseReader;
    private PlayerLevels playerLevels;
    private static final System.Logger logger = Util.getLogger(PlayerLevelsDAO.class.getName());

    @Override
    public DatabaseReader getDatabaseReader() {
        return databaseReader;
    }

    public PlayerLevelsDAO(DatabaseReader databaseReader) {
        this.databaseReader = databaseReader;
    }

    @Override
    public void preload() {
        playerLevels = getPlayerLevels();
    }

    public PlayerLevels getPlayerLevels() {
        if(playerLevels != null) {
            return playerLevels;
        }

        DbRecord r = getPlayerLevelRecord();
        if(r == null) {
            throw new IllegalStateException("playerlevels record not found in database");
        }
        Map<String, DbVariable> variables = r.getVariables();
        PlayerLevels p = new PlayerLevels();
        p.setSkillModifierPoints((Integer) variables.get("skillModifierPoints").getFirstValue());
        p.setManaIncrement((Integer) variables.get("manaIncrement").getFirstValue());
        p.setIntelligenceIncrement((Integer) variables.get("intelligenceIncrement").getFirstValue());
        p.setMaxPlayerLevel((Integer) variables.get("maxPlayerLevel").getFirstValue());
        p.setInitialSkillPoints((Integer) variables.get("initialSkillPoints").getFirstValue());
        p.setLifeIncrement((Integer) variables.get("lifeIncrement").getFirstValue());
        p.setDexterityIncrement((Integer) variables.get("dexterityIncrement").getFirstValue());
        p.setStrengthIncrement((Integer) variables.get("strengthIncrement").getFirstValue());
        p.setCharacterModifierPoints((Integer) variables.get("characterModifierPoints").getFirstValue());
        p.setExperienceLevelEquation((String) variables.get("experienceLevelEquation").getFirstValue());

        if(p.getIntelligenceIncrement() <= 0
                || p.getStrengthIncrement() <= 0
                || p.getDexterityIncrement() <= 0
                || p.getManaIncrement() <= 0
                || p.getLifeIncrement() <= 0) {
            throw new IllegalStateException("illegal value reading database (playerlevels)");
        }
        playerLevels = p;
        return playerLevels;
    }

    public DbRecord getPlayerLevelRecord() {
        List<DbVariable> dbVariables = Util.filterRecordVariables(getRecord(Constants.RECORD_PC_MALE),
                Database.Variables.LEVEL_FILE_NAME);

        if (dbVariables != null && !dbVariables.isEmpty()) {
            DbVariable v = dbVariables.get(0);
            if (v != null && v.getType() == DbVariable.Type.STRING && v.getValues().size() == 1) {
                DbRecord record = getRecord((String) dbVariables.get(0).getFirstValue());
                logger.log(System.Logger.Level.DEBUG, "playerLevels: found ''{0}''", record);
                return record;
            }
        }
        return null;
    }
}
