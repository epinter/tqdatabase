/*
 * Copyright (C) 2025 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.models;

import java.io.File;
import java.nio.file.Path;
import java.util.regex.Matcher;

public class ResourceFile {
    private final String name;
    private final byte[] data;
    private final ResourceType type;
    private final Path path;

    public ResourceFile(String name, byte[] data, ResourceType type) {
        this.name = name;
        this.data = data;
        this.type = type;
        this.path = Path.of(name.replace("\\", Matcher.quoteReplacement(File.separator)));
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }

    public ResourceType getResourceType() {
        return type;
    }

    public Path getPath() {
        return path;
    }
}
