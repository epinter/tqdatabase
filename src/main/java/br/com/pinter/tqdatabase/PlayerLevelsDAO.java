/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.models.DbVariable;
import br.com.pinter.tqdatabase.models.PlayerLevels;
import br.com.pinter.tqdatabase.util.Constants;
import br.com.pinter.tqdatabase.util.Util;

import java.util.Hashtable;
import java.util.List;

public class PlayerLevelsDAO implements BaseDAO {
    private ArzFile arzFile;
    private PlayerLevels playerLevels;
    private final System.Logger logger = Util.getLogger(PlayerLevelsDAO.class.getName());

    @Override
    public DbRecord getRecord(String recordPath) {
        return arzFile.getRecord(recordPath);
    }

    public PlayerLevelsDAO(ArzFile arzFile) {
        this.arzFile = arzFile;
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
        Hashtable<String, DbVariable> variables = r.getVariables();
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
            throw new RuntimeException("illegal value reading database (playerlevels)");
        }
        playerLevels = p;
        return playerLevels;
    }

    public DbRecord getPlayerLevelRecord() {
        List<DbVariable> dbVariables = Util.filterRecordVariables(getRecord(Constants.RECORD_PC),
                Database.Variables.LEVEL_FILE_NAME);

        if (dbVariables != null && dbVariables.size() > 0) {
            DbVariable v = dbVariables.get(0);
            if (v.getType() == DbVariable.Type.String && v.getValues().size() == 1) {
                DbRecord record = getRecord((String) dbVariables.get(0).getFirstValue());
                logger.log(System.Logger.Level.DEBUG, "playerLevels: found ''{0}''", record);
                return record;
            }
        }
        return null;
    }
}
