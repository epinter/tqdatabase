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

public class BaseType {
    private String recordPath;
    private String className;
    private String actorName;
    private String fileDescription;
    private DbRecord dbRecord;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getRecordPath() {
        return recordPath;
    }

    public void setRecordPath(String recordPath) {
        this.recordPath = DbRecord.normalizeRecordPath(recordPath);
    }

    public DbRecord getDbRecord() {
        return dbRecord;
    }

    public void setDbRecord(DbRecord dbRecord) {
        this.dbRecord = dbRecord;
    }

    public String getFileDescription() {
        return fileDescription;
    }

    public void setFileDescription(String fileDescription) {
        this.fileDescription = fileDescription;
    }

    @Override
    public String toString() {
        return "BaseType{" +
                "recordPath='" + recordPath + '\'' +
                ", className='" + className + '\'' +
                ", actorName='" + actorName + '\'' +
                ", fileDescription='" + fileDescription + '\'' +
                '}';
    }
}
