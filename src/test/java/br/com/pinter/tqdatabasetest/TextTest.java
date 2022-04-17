/*
 * Copyright (C) 2022 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabasetest;

import br.com.pinter.tqdatabase.Text;
import br.com.pinter.tqdatabase.cache.CacheText;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TextTest {
    private Text text;

    @BeforeEach
    void setUp() throws Exception {
        CacheText.getInstance().clear();
        if (!new File("src/test/resources/Text_EN.arc").exists()) {
            throw new IOException("File src/test/resources/Text_EN.arc doesnt exists." +
                    " Copy the .arc file to execute to test");
        }
        text = new Text(new String[]{"src/test/resources"});
    }

    @Test
    void getString_Given_Tag_Then_returnText() throws IOException {
        assertNotNull(text.getString("tagGreeceQ1UiTitle"));
        assertNotEquals("tagGreeceQ1UiTitle", text.getString("tagGreeceQ1UiTitle"));
        CacheText.getInstance().clear();
    }

    @Test
    void loadText_Should_loadAllStringsAndReturnNull() throws IOException {
        assertNull(text.getString(null));
        assertTrue(CacheText.getInstance().size() > 2);
    }
}