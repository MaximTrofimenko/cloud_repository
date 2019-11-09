package com.trofimenko.cloud.server;

import com.trofimenko.cloud.common.FileDeleteMessage;
import com.trofimenko.cloud.common.FileListMessage;
import com.trofimenko.cloud.common.FileMessage;
import com.trofimenko.cloud.common.FileRequest;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class Request {
    //если клиент хочет получить файл(запрос на скачивание)
    static FileMessage fileRequest(FileRequest fr) {
        if (Files.exists(Paths.get("server_storage/" + fr.getFilename()))) {
            //если такой файл есть то мы по нему формируем Filemessage, формируем его по конструктору из примера и затягиваем туда бауты в байт массив. Здесь связка между
            //файл реквест и между файл мессадж
            FileMessage fm = null;
            try {
                fm = new FileMessage(Paths.get("server_storage/" + fr.getFilename()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //пульнули клиенту готовый FileMessage
           return fm;
        }
        return null;
    }

    //загрузкa файла на сервак
    static void fileMessage(FileMessage fm) {
        try {
            Files.write(Paths.get("server_storage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //удаление файла с сервера
    static void fileDeleteMessage(FileDeleteMessage fdm)  {
        try {
            Files.delete(Paths.get("server_storage/" + fdm.getFilename()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //получить список файлов на серваке
   static ArrayList<String> getArrFileInStorage() {
        ArrayList<String> arr = new ArrayList<>();
        try {
            Files.list(Paths.get("server_storage"))
                    .map(p -> p.getFileName().toString())
                    .forEach(arr::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arr;
    }
}
