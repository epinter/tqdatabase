/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.models;

import br.com.pinter.tqdatabase.util.Util;

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
        this.recordPath = Util.normalizeRecordPath(recordPath);
    }

    public DbRecord getDbRecord() {
        return dbRecord;
    }

    public void setDbRecord(DbRecord dbRecord) {
        this.dbRecord = dbRecord;
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
