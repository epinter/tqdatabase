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
    void getString_Given_Tag_Then_returnText2() throws IOException {
        assertNotNull(text.getString("xtagxSQLocation07"));
        assertNotEquals("xtagxSQLocation07", text.getString("xtagxSQLocation07"));
        CacheText.getInstance().clear();
    }

    @Test
    void loadText_Should_loadAllStringsAndReturnNull() throws IOException {
        assertNull(text.getString(null));
        assertTrue(CacheText.getInstance().size() > 2);
    }
}