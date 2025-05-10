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

package br.com.pinter.tqdatabase.models;

import br.com.pinter.tqdatabase.Database;

import java.util.*;

public class DbRecord {
    private String id;
    private int stringIndex;
    private String recordType;
    private int offset;
    private Map<String, DbVariable> variables = new LinkedHashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStringIndex() {
        return stringIndex;
    }

    public void setStringIndex(int stringIndex) {
        this.stringIndex = stringIndex;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public Map<String, DbVariable> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, DbVariable> variables) {
        this.variables = variables;
    }

    public Object getFirstValue(String variable) {
        if (variables.get(variable) != null) {
            return variables.get(variable).getFirstValue();
        }
        return null;
    }

    public List<String> getListString(String variable) {
        if (variables.get(variable) != null) {
            return variables.get(variable).getListString();
        }
        return Collections.emptyList();
    }

    public List<Integer> getListInteger(String variable) {
        if (variables.get(variable) != null) {
            return variables.get(variable).getListInteger();
        }
        return Collections.emptyList();
    }

    public String getRecordClass() {
        return (String) getFirstValue(Database.Variables.CLASS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DbRecord dbRecord = (DbRecord) o;
        return stringIndex == dbRecord.stringIndex && offset == dbRecord.offset && id.equals(dbRecord.id) && recordType.equals(dbRecord.recordType) && variables.equals(dbRecord.variables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stringIndex, recordType, offset, variables);
    }

    public List<String> asFile() {
        return variables.values().stream().map(DbVariable::asLine).toList();
    }

    @Override
    public String toString() {
        return "Record{" +
                "id='" + id + '\'' +
                ", stringIndex=" + stringIndex +
                ", recordType='" + recordType + '\'' +
                ", offset=" + offset +
                ", variables=" + variables +
                '}';
    }
}
