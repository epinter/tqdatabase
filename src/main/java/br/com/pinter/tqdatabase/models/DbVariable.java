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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class DbVariable {
    private String variableName;
    private DbVariable.Type type;
    private final List<Object> values = new LinkedList<>();

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public boolean hasValues() {
        return !values.isEmpty();
    }

    public int valuesCount() {
        return values.size();
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
        return values.getFirst();
    }

    public String getFirstString() {
        if (type == Type.STRING) {
            return (String) values.getFirst();
        }
        throw new IllegalStateException("DbVariable not a string");
    }

    public Integer getFirstInteger() {
        if (type == Type.INTEGER) {
            return (Integer) values.getFirst();
        }
        throw new IllegalStateException("DbVariable not a string");
    }

    public List<String> getListString() {
        if (type == Type.STRING) {
            return values.stream().map(f -> (String) f).toList();
        }
        return Collections.emptyList();
    }

    public List<Integer> getListInteger() {
        if (type == Type.INTEGER) {
            return values.stream().map(f -> (Integer) f).toList();
        }
        return Collections.emptyList();
    }

    public List<Float> getListFloat() {
        if (type == Type.FLOAT) {
            return values.stream().map(f -> (Float) f).toList();
        }
        return Collections.emptyList();
    }

    public List<Boolean> getListBoolean() {
        if (type == Type.BOOLEAN) {
            return values.stream().map(f -> (Boolean) f).toList();
        }
        return Collections.emptyList();
    }

    public String asLine() {
        String key = "";
        StringBuilder value = new StringBuilder();
        key = getVariableName();

        switch (type) {
            case STRING -> value.append(String.join(";", getListString()));
            case INTEGER -> value.append(String.join(";", getListInteger().stream().map(String::valueOf).toList()));
            case FLOAT -> value.append(String.join(";", getListFloat().stream()
                    .map(f -> String.format(Locale.ENGLISH, "%.6f", f))
                    .toList()));
            case BOOLEAN -> value.append(String.join(";", getListBoolean().stream()
                    .map(f -> f ? "1" : "0")
                    .toList()));
            default -> throw new IllegalArgumentException("unknown value");
        }
        return String.format("%s,%s,", key, value);
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
