/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.models;

import br.com.pinter.tqdatabase.Database;

import java.util.Hashtable;
import java.util.List;

public class DbRecord {
    private String id;
    private int stringIndex;
    private String recordType;
    private int offset;

    private Hashtable<String, DbVariable> variables = new Hashtable<>();

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

    public Hashtable<String, DbVariable> getVariables() {
        return variables;
    }

    public void setVariables(Hashtable<String, DbVariable> variables) {
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
        return null;
    }

    public List<Integer> getListInteger(String variable) {
        if (variables.get(variable) != null) {
            return variables.get(variable).getListInteger();
        }
        return null;
    }

    public String getRecordClass() {
        return (String) getFirstValue(Database.Variables.CLASS);
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
