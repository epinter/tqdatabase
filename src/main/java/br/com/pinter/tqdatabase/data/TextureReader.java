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

package br.com.pinter.tqdatabase.data;

import br.com.pinter.tqdatabase.models.Texture;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;

public class TextureReader implements ArcEntryReader<Texture> {
    @Override
    public Texture readAll(ResourceReader resourceReader) throws IOException {
        throw new NotImplementedException("not implemented");
    }

    @Override
    public Texture readFile(ResourceReader resourceReader, String filename) throws IOException {
        if (SystemUtils.IS_OS_WINDOWS) {
            return new Texture(filename, resourceReader.getData(filename));
        } else {
            throw new NotImplementedException("Only Windows OS is supported");
        }
    }
}
