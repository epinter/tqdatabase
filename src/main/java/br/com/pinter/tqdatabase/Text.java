/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.cache.CacheText;
import br.com.pinter.tqdatabase.util.BOM;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

/**
 * Class to access Text resources from game
 */
@SuppressWarnings("UnusedReturnValue")
public class Text {
    private final Hashtable<String, String> tags;
    private final String lang;
    private final String path;
    private final boolean useCache;

    /**
     * Constructor for english (EN) text resources.
     *
      * @param path Absolute path for the Text directory containing all Text_* files.
     */
    public Text(String path) {
        this(path, "EN", true);
    }


    /**
     * Constructor for text resources from specific language
     *
     * @param path Absolute path for the Text directory containing all Text_* files.
     * @param lang The language to load text resources
     */
    public Text(String path, String lang) {
        this(path, lang, true);
    }

    /**
     * @param path Absolute path for the Text directory containing all Text_* files.
     * @param lang The language to load text resources
     * @param useCache Disable cache
     */
    private Text(String path, String lang, boolean useCache) {
        this.tags = new Hashtable<>();
        this.path = path;
        this.useCache = useCache;
        this.lang = lang.toUpperCase();
    }

    /**
     * Method to preload all strings
     */
    public void preload() throws IOException {
        getString(null);
    }

    /**
     * Search for a specific string (<b><code>tag</code></b>) in the loaded text resource
     *
     * @param tag The tag to search for
     * @return returns the string associated to the <b><code>tag</code></b>
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public String getString(String tag) throws IOException {
        if ((useCache && CacheText.getInstance().isEmpty()) || (!useCache && tags.isEmpty())) {
            loadText();
        }

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

    private void loadText() throws IOException {
        ArcFile arcFile;
        String filename;
        if (lang.matches("[A-Z]{2}")) {
            filename = String.format("%s/Text_%s.arc", path, lang);
        } else {
            filename = String.format("%s/Text_EN.arc", path);
        }
        arcFile = new ArcFile(filename);

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
