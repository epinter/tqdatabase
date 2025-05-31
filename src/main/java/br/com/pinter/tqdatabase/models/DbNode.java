/*
 * Copyright (C) 2025 Emerson Pinter - All Rights Reserved
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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DbNode {
    private final Path name;
    private final List<DbNode> children = new ArrayList<>();
    private DbRecord record;

    public DbNode(Path name) {
        this.name = name;
        this.record = null;
    }

    public DbNode(Path name, DbRecord record) {
        this.name = name;
        this.record = record;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DbNode dbNode)) return false;
        return Objects.equals(name, dbNode.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    public void addChild(DbNode dbNode) {
        children.add(dbNode);
    }

    public List<DbNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public Path getName() {
        return name;
    }

    public DbRecord getRecord() {
        return record;
    }

    public void setRecord(DbRecord record) {
        this.record = record;
    }

    public boolean isRecordLoaded() {
        return this.record != null;
    }

    @Override
    public String toString() {
        return name.toString();
    }
}
