package com.trofimenko.cloud.common;

public class FileDeleteMessage extends AbstractMessage {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public FileDeleteMessage(String filename) {
        this.filename = filename;
    }
}
