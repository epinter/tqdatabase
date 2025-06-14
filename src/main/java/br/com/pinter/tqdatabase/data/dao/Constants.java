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

package br.com.pinter.tqdatabase.data.dao;

final class Constants {
    private Constants() {
    }

    public static final String RECORD_PC_LEGACY = "records\\creature\\pc\\malepc01.dbr";
    public static final String RECORD_PC_MALE = "records\\xpack\\creatures\\pc\\malepc01.dbr";
    public static final String RECORD_PC_FEMALE = "records\\xpack\\creatures\\pc\\femalepc01.dbr";

    public static final String REGEXP_FIELD_SKILLTREE = "(?i:^skilltree\\d+$)";
    public static final String REGEXP_FIELD_SKILLNAME = "(?i:^skillname\\d+$)";
    public static final String REGEXP_FIELD_SKILLLEVEL = "(?i:^skilllevel\\d+$)";
}

