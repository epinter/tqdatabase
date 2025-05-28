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
import br.com.pinter.tqdatabase.data.TextureConverter;
import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.models.ResourceFile;
import br.com.pinter.tqdatabase.models.ResourceType;
import br.com.pinter.tqdatabase.models.Texture;
import br.com.pinter.tqdatabase.models.TextureType;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class to access resources from game
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class Resources implements TQService {
    private static final System.Logger logger = System.getLogger(Resources.class.getName());
    private final Path path;
    private final ResourceReader reader;

    /**
     * @param path Path of an ARC file
     */
    public Resources(Path path) throws IOException {
        this.path = path;
        this.reader = ResourceReader.builder(path.toAbsolutePath()).withCache(false).build();
    }

    /**
     * @param path Path of an ARC file
     */
    public Resources(String path) throws IOException {
        this(Path.of(path));
    }

    @Override
    public void preload() throws IOException {
        throw new NotImplementedException("not implemented");
    }

    public Path getPath() {
        return path;
    }

    public List<String> getNames() {
        return new ArrayList<>(reader.list());
    }

    public ResourceFile getFile(String resourceName) throws IOException {
        if (reader.list().contains(DbRecord.normalizeRecordPath(resourceName))) {
            ResourceType rt = ResourceType.of(resourceName);
            if (rt == ResourceType.TEXTURE) {
                return new Texture(resourceName, reader.getData(resourceName));
            } else {
                return new ResourceFile(resourceName, reader.getData(resourceName), rt);
            }
        }

        return null;
    }

    public byte[] getTextureAsDds(String name) throws IOException {
        return new TextureConverter(reader.readTexture(name)).convert(TextureType.DDS, false).getData();
    }

    public Map<String, ResourceFile> getAll() throws IOException {
        return getAllFromArchive(null);
    }

    public Map<String, byte[]> getAllFonts() throws IOException {
        return getAllFromArchive(ResourceType.FONT).entrySet()
                .stream().collect(Collectors.toMap(
                        Map.Entry::getKey, e -> e.getValue().getData())
                );
    }

    public Map<String, ResourceFile> getAllFromArchive(ResourceType type) throws IOException {
        Map<String, ResourceFile> ret = new HashMap<>();

        try {
            for (String resourceName : type == null ? reader.list() : reader.list(type)) {
                ResourceType rt = ResourceType.of(resourceName);
                if (rt == ResourceType.TEXTURE) {
                    ret.put(resourceName, new Texture(resourceName, reader.getData(resourceName)));
                } else {
                    ret.put(resourceName, new ResourceFile(resourceName, reader.getData(resourceName), rt));
                }
            }
        } catch (EntryNotFoundException e) {
            throw new IOException(e);
        }

        return ret;
    }
}
