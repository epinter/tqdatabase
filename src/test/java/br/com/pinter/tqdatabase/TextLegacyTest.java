/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.cache.CacheText;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class TextLegacyTest {
    private Text text;

    @Before
    public void setUp() throws Exception {
        CacheText.getInstance().clear();
        String tqText = "src/test/resources/disc_tqit/Text-tq";
        String tqitText = "src/test/resources/disc_tqit/Resources-tqit";
        if (!new File(tqText).exists() || !new File(tqitText).exists()) {
            throw new IOException(String.format("Files '%s' or '%s' doesnt exists." +
                    " Copy the .arc files to execute to test", tqText, tqitText));
        }
        text = new Text(new String[]{tqText,tqitText});
    }

    @Test
    public void getString_Given_Tag_Then_returnText() throws IOException {
        assertNotNull(text.getString("xtagxSQLocation07"));
        assertNotEquals(text.getString("xtagxSQLocation07"), "xtagxSQLocation07");
        CacheText.getInstance().clear();
    }

    @Test
    public void loadText_Should_loadAllStringsAndReturnNull() throws IOException {
        assertNull(text.getString(null));
        assertTrue(CacheText.getInstance().size() > 2);
    }
}