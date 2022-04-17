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
import br.com.pinter.tqdatabase.data.dao.TeleportDAO;
import br.com.pinter.tqdatabase.models.Teleport;

public class Teleports implements TQService {
    private final TeleportDAO teleportDAO;

    @Override
    public void preload() {
        //unused
    }

    Teleports(DatabaseReader databaseReader) {
        teleportDAO = new TeleportDAO(databaseReader);
    }

    public Teleport getTeleport(String recordPath) {
        return teleportDAO.getTeleport(recordPath);
    }
}
