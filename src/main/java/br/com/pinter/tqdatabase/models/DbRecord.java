/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.models;

import br.com.pinter.tqdatabase.Database;

import java.util.*;

public class DbRecord {
    private String id;
    private int stringIndex;
    private String recordType;
    private int offset;

    private Map<String, DbVariable> variables = new HashMap<>();

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
