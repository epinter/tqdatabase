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

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.data.ResourceReader;
import br.com.pinter.tqdatabase.util.Util;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to access resources from game
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class Resources implements TQService {
    private static final System.Logger logger = Util.getLogger(Resources.class.getName());
    private final String path;

    /**
     * @param path Resources directory path
     */
    public Resources(String path) {
        this.path = path;
    }

    /**
     * Method to preload resources
     */
    public void preload() throws IOException {
        throw new NotImplementedException("not implemented");
    }

    public Map<String, byte[]> getAllFonts() {
        Map<String, byte[]> fonts = new HashMap<>();

        try {
            ResourceReader resource = ResourceReader.builder(path).withCache(false).build();
            for (String resourceName : resource.list()) {
                fonts.put(resourceName, resource.getData(resourceName));
            }
        } catch (IOException e) {
            logger.log(System.Logger.Level.ERROR, "Error", e);
        }

        return fonts;
    }
}
