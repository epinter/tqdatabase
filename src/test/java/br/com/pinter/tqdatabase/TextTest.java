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

public class TextTest {
    private Text text;

    @Before
    public void setUp() throws Exception {
        if (!new File("src/test/resources/Text_EN.arc").exists()) {
            throw new IOException("File src/test/resources/Text_EN.arc doesnt exists." +
                    " Copy the .arc file to execute to test");
        }
        text = new Text("src/test/resources");
    }

    @Test
    public void getString_Given_Tag_Then_returnText() {
        assertNotNull(text.getString("tagGreeceQ1UiTitle"));
        assertNotEquals(text.getString("tagGreeceQ1UiTitle"), "tagGreeceQ1UiTitle");
        CacheText.getInstance().clear();
    }

    @Test
    public void loadText_Should_loadAllStringsAndReturnNull() {
        assertNull(text.getString(null));
        assertTrue(CacheText.getInstance().size() > 2);
    }
}