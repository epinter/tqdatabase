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

package br.com.pinter.tqdatabase.util;

import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.models.DbVariable;

import java.util.List;
import java.util.stream.Collectors;

public class Util {
    public static System.Logger getLogger(String name) {
        return System.getLogger(name);
    }

    private Util() {
    }


    public static List<DbVariable> filterRecordVariables(DbRecord r, String regexp) {
        if (r == null) {
            return null;
        }
        return r.getVariables().values().stream().filter(
                e -> e.getVariableName().matches(regexp)).collect(Collectors.toList());
    }


    public static String normalizeRecordPath(String recordId) {
        if (recordId == null || recordId.isEmpty()) {
            return null;
        }
        return recordId.toUpperCase().replace("/", "\\");
    }

}
