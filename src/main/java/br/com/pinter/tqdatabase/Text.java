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

import br.com.pinter.tqdatabase.cache.CacheText;
import br.com.pinter.tqdatabase.data.ResourceReader;
import br.com.pinter.tqdatabase.util.Util;

import java.io.*;
import java.util.*;

/**
 * Class to access Text resources from game
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class Text implements TQService {
    private static final System.Logger logger = Util.getLogger(Text.class.getName());

    private final Map<String, String> tags;
    private final String lang;
    private final List<String> pathList;
    private final boolean useCache;

    /**
     * Constructor for english (EN) text resources.
     *
      * @param paths Array of ordered absolute paths for the Text directories containing all Text_* files.
     *              Strings from files loaded last will override those loaded first.
     */
    public Text(String[] paths) {
        this(paths, "EN", true);
    }


    /**
     * Constructor for text resources from specific language
     *
     * @param paths Array of ordered absolute paths for the Text directories containing all Text_* files.
     *              Strings from files loaded last will override those loaded first.
     * @param lang The language to load text resources
     */
    public Text(String[] paths, String lang) {
        this(paths, lang, true);
    }

    /**
     * @param paths Array of ordered absolute paths for the Text directories containing all Text_* files.
     *              Strings from files loaded last will override those loaded first.
     * @param lang The language to load text resources
     * @param useCache Disable cache
     */
    private Text(String[] paths, String lang, boolean useCache) {
        this.tags = new HashMap<>();
        this.pathList = Arrays.asList(paths);
        this.useCache = useCache;
        this.lang = lang.toUpperCase();

    }

    /**
     * Method to preload all strings
     */
    public void preload() throws IOException {
        if ((useCache && CacheText.getInstance().isEmpty()) || (!useCache && tags.isEmpty())) {
            loadTextFromAllPaths();
        }
    }

    /**
     * Search for a specific string (<b><code>tag</code></b>) in the loaded text resource
     *
     * @param tag The tag to search for
     * @return returns the string associated to the <b><code>tag</code></b>
     */
    public String getString(String tag) throws IOException {
        preload();

        if (tag == null) {
            return null;
        }

        if (useCache) {
            if (CacheText.getInstance().get(tag) == null && this.tags.get(tag) != null) {
                CacheText.getInstance().put(tag, tags.get(tag));
            }
            return CacheText.getInstance().get(tag);
        } else {
            return tags.get(tag);
        }
    }

    private void loadTextFromAllPaths() throws IOException {
        for (String p: pathList) {
            loadText(resolveArcFilename(p));
        }
    }

    private String resolveArcFilename(String path) {
        String filename = null;
        if (lang != null && lang.matches("[A-Z]{2}")) {
            filename = String.format("%s/Text_%s.arc", path, lang);
        }

        if((filename == null || filename.isEmpty()) || !new File(filename).exists()) {
            filename = String.format("%s/Text_EN.arc", path);
        }
        return filename;
    }

    private void loadText(String filename) throws IOException {
        ResourceReader resource = ResourceReader.builder(filename).withCache(useCache).build();
        this.tags.putAll(resource.readText());
    }
}
