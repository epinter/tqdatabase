/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.models;

import java.util.*;

public class DbVariable {
    private String variableName;
    private DbVariable.Type type;
    private final List<Object> values = new ArrayList<>();

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public List<Object> getValues() {
        return values;
    }

    public void addValue(Object value) {
        this.values.add(value);
    }

    public DbVariable.Type getType() {
        return type;
    }

    public void setType(DbVariable.Type type) {
        this.type = type;
    }

    public Object getFirstValue() {
        return values.get(0);
    }

    public List<String> getListString() {
        if (type == Type.STRING) {
            List<String> list = new ArrayList<>();
            values.forEach(f -> list.add((String) f));
            return list;
        }
        return Collections.emptyList();
    }

    public List<Integer> getListInteger() {
        if (type == Type.INTEGER) {
            List<Integer> list = new ArrayList<>();
            values.forEach(f -> list.add((Integer) f));
            return list;
        }
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "DbVariable{" +
                "variableName='" + variableName + '\'' +
                ", values=" + values +
                '}';
    }

    public enum Type {
        UNKNOWN(4),
        STRING(2),
        INTEGER(0),
        FLOAT(1),
        BOOLEAN(3);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public static Type valueOf(int value) {
            Optional<Type> o = Arrays.stream(values()).filter(v -> v.value == value).findFirst();
            return o.orElse(Type.UNKNOWN);
        }
    }
}
