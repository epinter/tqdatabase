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
        return values.get(0);
    }

    public String getFirstString() {
        if(type == Type.STRING) {
            return (String) values.get(0);
        }
        throw new IllegalStateException("DbVariable not a string");
    }

    public Integer getFirstInteger() {
        if(type == Type.INTEGER) {
            return (Integer) values.get(0);
        }
        throw new IllegalStateException("DbVariable not a string");
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
