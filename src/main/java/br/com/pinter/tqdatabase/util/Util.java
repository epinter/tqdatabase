/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.util;

import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.models.DbVariable;

import java.util.List;
import java.util.stream.Collectors;

public class Util {

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
