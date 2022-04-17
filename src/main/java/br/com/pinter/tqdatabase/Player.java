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

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.data.DatabaseReader;
import br.com.pinter.tqdatabase.data.dao.PcDAO;
import br.com.pinter.tqdatabase.data.dao.PlayerLevelsDAO;
import br.com.pinter.tqdatabase.models.Pc;
import br.com.pinter.tqdatabase.models.PlayerLevels;

public class Player implements TQService {
    private final PlayerLevelsDAO playerLevelsDAO;
    private final PcDAO pcDAO;

    public Player(DatabaseReader databaseReader) {
        playerLevelsDAO = new PlayerLevelsDAO(databaseReader);
        pcDAO = new PcDAO(databaseReader);
    }

    @Override
    public void preload() {
        playerLevelsDAO.preload();
        pcDAO.preload();
    }

    public PlayerLevels getPlayerLevels() {
        return playerLevelsDAO.getPlayerLevels();
    }

    public Pc getPc(Pc.Gender gender) {
        return pcDAO.getPc(gender);
    }

    public Pc getPc() {
        return pcDAO.getPc();
    }

}
