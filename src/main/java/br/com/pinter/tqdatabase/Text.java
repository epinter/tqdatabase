/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.cache.CacheText;
import br.com.pinter.tqdatabase.util.BOM;

import java.io.*;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 * Class to access Text resources from game
 */
@SuppressWarnings("UnusedReturnValue")
public class Text {
    private final Hashtable<String, String> tags;
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
        this.tags = new Hashtable<>();
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
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public String getString(String tag) throws IOException {
        loadTextFromAllPaths();

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
        ArcFile arcFile = new ArcFile(filename);

        for (String tf : arcFile.listRecords()) {
            byte[] d = arcFile.getData(tf);
            try {
                BufferedReader br = new BufferedReader(new StringReader(new String(d, BOM.toCharset(d))));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("//")) {
                        continue;
                    }
                    line = line.replaceAll("//.*", "").trim();

                    String[] kv = line.split("=");
                    if (kv.length == 2 && kv[0] != null && kv[1] != null) {
                        if (useCache) {
                            CacheText.getInstance().put(kv[0], kv[1]);
                        } else {
                            tags.put(kv[0], kv[1]);
                        }
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
