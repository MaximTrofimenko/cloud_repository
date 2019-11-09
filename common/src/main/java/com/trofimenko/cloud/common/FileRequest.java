package com.trofimenko.cloud.common;
//запрос файла на скачивание
public class FileRequest extends AbstractMessage {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public FileRequest(String filename) {
        this.filename = filename;
    }
}
