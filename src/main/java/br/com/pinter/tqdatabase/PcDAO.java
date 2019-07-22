/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.models.DbVariable;
import br.com.pinter.tqdatabase.models.Pc;
import br.com.pinter.tqdatabase.util.Constants;
import br.com.pinter.tqdatabase.util.Util;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class PcDAO implements BaseDAO {
    private ArzFile arzFile;
    private Pc pc;
    private final System.Logger logger = Util.getLogger(PcDAO.class.getName());

    @Override
    public DbRecord getRecord(String recordPath) {
        return arzFile.getRecord(recordPath);
    }

    public PcDAO(ArzFile arzFile) {
        this.arzFile = arzFile;
    }

    @Override
    public void preload() {
        pc = getPc();
    }

    public Pc getPc() {
        if (pc != null) {
            return pc;
        }
        Pc p = new Pc();
        DbRecord r = getRecord(Constants.RECORD_PC);
        List<DbVariable> skillTreeVars = Util.filterRecordVariables(r, Constants.REGEXP_FIELD_SKILLTREE);
        Hashtable<String, DbVariable> dbVariables = r.getVariables();

        if (dbVariables != null && dbVariables.size() > 0) {
            p.setCharacterLife((Float) dbVariables.get("characterLife").getFirstValue());
            p.setCharacterMana((Float) dbVariables.get("characterMana").getFirstValue());
            p.setCharacterStrength((Float) dbVariables.get("characterStrength").getFirstValue());
            p.setCharacterIntelligence((Float) dbVariables.get("characterIntelligence").getFirstValue());
            p.setCharacterDexterity((Float) dbVariables.get("characterDexterity").getFirstValue());
            HashMap<String, String> list = new HashMap<>();

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
                    || p.getSkillTreeTable().size() == 0) {
                throw new RuntimeException("illegal value reading database (pc)");
            }
        }
        logger.log(System.Logger.Level.DEBUG, "pc: found ''{0}''", p);
        return p;
    }
}

