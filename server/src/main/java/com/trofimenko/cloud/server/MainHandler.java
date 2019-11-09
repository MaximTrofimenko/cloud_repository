package com.trofimenko.cloud.server;

import com.trofimenko.cloud.common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class MainHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            //если клиент хочет получить файл(запрос на скачивание)
            if (msg instanceof FileRequest) {
                ctx.writeAndFlush(Request.fileRequest((FileRequest) msg));
            }

//            if (msg instanceof FileRequest) {
//                //кастуем msg к файл реквесту
//                FileRequest fr = (FileRequest) msg;
//                //ищем в хранилище файл с таким именем
//                if (Files.exists(Paths.get("server_storage/" + fr.getFilename()))) {
//                    //если такой файл есть то мы по нему формируем Filemessage, формируем его по конструктору из примера и затягиваем туда бауты в байт массив. Здесь связка между
//                    //файл реквест и между файл мессадж
//                    FileMessage fm = new FileMessage(Paths.get("server_storage/" + fr.getFilename()));
//                    //пульнули клиенту готовый FileMessage
//                    ctx.writeAndFlush(fm);
//                }
//            }

            //загрузкa файла на сервак
            if (msg instanceof FileMessage) {
                Request.fileMessage((FileMessage) msg);
                ctx.writeAndFlush(new FileListMessage(Request.getArrFileInStorage()));
            }

            //удаление файла с сервера
            if (msg instanceof FileDeleteMessage){
                Request.fileDeleteMessage((FileDeleteMessage) msg);
//                FileDeleteMessage fdm = (FileDeleteMessage) msg;
//                Files.delete(Paths.get("server_storage/" + fdm.getFilename()));
                ctx.writeAndFlush(new FileListMessage(Request.getArrFileInStorage()));
            }


            //отправка списка файлов на сервер при инициализации клиента
            if (msg instanceof FileRequestInitial){
                ctx.writeAndFlush(new FileListMessage(Request.getArrFileInStorage()));
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }



}
