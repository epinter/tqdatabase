/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.models;

import java.util.*;

public class DbVariable {
    private String variableName;
    private DbVariable.Type type;
    private final ArrayList<Object> values = new ArrayList<>();

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public ArrayList<Object> getValues() {
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
        if (type == Type.String) {
            List<String> list = new ArrayList<>();
            values.forEach(f -> list.add((String) f));
            return list;
        }
        return Collections.emptyList();
    }

    public List<Integer> getListInteger() {
        if (type == Type.Integer) {
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
        Unknown(4),
        String(2),
        Integer(0),
        Float(1),
        Boolean(3);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public static Type valueOf(int value) {
            Optional<Type> o = Arrays.stream(values()).filter(v -> v.value == value).findFirst();
            return o.orElse(Type.Unknown);
        }
    }
}
