package com.trofimenko.cloud.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

//это когда к нам прилетит файл то он будет таким.
//для юольших файлов это не эффективно. тут нужно думать над оптимизацией
public class FileMessage extends AbstractMessage {
    private String filename;
    private byte[] data;

    public String getFilename() {
        return filename;
    }

    public byte[] getData() {
        return data;
    }

    public FileMessage(Path path) throws IOException {
        filename = path.getFileName().toString();
        data = Files.readAllBytes(path);
    }
}
