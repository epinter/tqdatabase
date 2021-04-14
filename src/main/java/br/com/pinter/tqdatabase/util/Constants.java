/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.util;

public class Constants {
    public static final String RECORD_PC_LEGACY = "records\\creature\\pc\\malepc01.dbr";
    public static final String RECORD_PC_MALE = "records\\xpack\\creatures\\pc\\malepc01.dbr";
    public static final String RECORD_PC_FEMALE = "records\\xpack\\creatures\\pc\\femalepc01.dbr";

    public static final String REGEXP_PATH_SKILLTREE = "(?i:^records(\\\\xpack.*)?\\\\skills\\\\.*dbr)";
    public static final String REGEXP_PATH_SKILL = "(?i:^records(\\\\xpack.*)?\\\\skills(?!\\\\old|shrine)\\\\.*dbr)";
    public static final String REGEXP_FIELD_SKILLTREE = "(?i:^skilltree\\d+$)";
    public static final String REGEXP_FIELD_SKILLNAME = "(?i:^skillname\\d+$)";
    public static final String REGEXP_FIELD_SKILLLEVEL = "(?i:^skilllevel\\d+$)";
}

