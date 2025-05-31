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
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public enum ResourceType {
    TEXT("TXT"),
    MAP("MAP"),
    TEXTURE("TEX", "DDS"),
    MESH("MSH"),
    ANIMATION("ANM"),
    AUDIO("WAV", "MP3"),
    EFFECT("PFX"),
    SHADER("SSH"),
    QUEST("QST"),
    FONT("FNT");


    ResourceType(String... extensions) {
        this.extensions = List.of(extensions);
    }

    private final List<String> extensions;

    public List<String> getExtensions() {
        return extensions;
    }

    public static ResourceType of(String name) {
        Optional<ResourceType> type = Arrays.stream(ResourceType.values()).filter(t ->
                t.getExtensions().stream().anyMatch(e ->
                        name.toUpperCase(Locale.ROOT).endsWith("." + e)
                )).findFirst();
        return type.orElse(null);

    }
}
