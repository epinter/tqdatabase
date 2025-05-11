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

import br.com.pinter.tqdatabase.EntryNotFoundException;
import br.com.pinter.tqdatabase.models.ResourceType;
import br.com.pinter.tqdatabase.models.Texture;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResourceReader {
    private final boolean useCache;
    private final ArcFile archive;

    private ResourceReader(Path archiveFilename, boolean useCache) throws IOException {
        this.useCache = useCache;
        this.archive = new ArcFile(archiveFilename);
    }

    public List<String> list() {
        return archive.listRecords();
    }

    public List<String> list(ResourceType resourceType) {
        return archive.listRecords().stream().filter(f ->
                        resourceType.getExtensions().stream().anyMatch(e ->
                                f.toUpperCase().endsWith(e.toUpperCase())))
                .collect(Collectors.toList());
    }

    public boolean isUseCache() {
        return useCache;
    }

    public static Builder builder(Path arcFilename) {
        return new Builder(arcFilename);
    }

    public byte[] getData(String resourceName) throws EntryNotFoundException {
        byte[] data = archive.getData(resourceName);
        if (data == null) {
            throw new EntryNotFoundException(String.format("Resource '%s' was not found in archive '%s'",
                    resourceName, archive.getArcFileName()));
        }
        return data;
    }

    private <T> T read(ArcEntryReader<T> type) throws IOException {
        return type.readAll(this);
    }

    private <T> T read(ArcEntryReader<T> type, String resourceName) throws IOException {
        return type.readFile(this, resourceName);
    }

    public Map<String, String> readText() throws IOException {
        return read(new TextReader());
    }

    public Texture readTexture(String resourceName) throws IOException {
        return read(new TextureReader(), resourceName);
    }

    public static class Builder {
        private boolean useCache = false;
        private final Path archiveFilename;

        public Builder(Path archiveFilename) {
            this.archiveFilename = archiveFilename;
        }

        public Builder withCache(boolean useCache) {
            this.useCache = useCache;
            return this;
        }

        public ResourceReader build() throws IOException {
            return new ResourceReader(archiveFilename, useCache);
        }
    }
}
