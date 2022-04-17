/*
 * Copyright (C) 2022 Emerson Pinter - All Rights Reserved
 */

/*    This file is part of TQ Database.

    TQ Respec is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    TQ Database is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with TQ Respec.  If not, see <http://www.gnu.org/licenses/>.
*/

package br.com.pinter.tqdatabase.models;

import java.util.List;

public enum ResourceType {
    TEXT("TXT"),
    MAP("MAP"),
    TEXTURE("TEX"),
    MESH("MSH"),
    ANIMATION("ANM"),
    AUDIO("WAV", "MP3"),
    EFFECT("PFX"),
    SHADER("SSH"),
    QUEST("QST");

    private final List<String> extensions;

    ResourceType(String... extensions) {
        this.extensions = List.of(extensions);
    }

    public List<String> getExtensions() {
        return extensions;
    }
}
