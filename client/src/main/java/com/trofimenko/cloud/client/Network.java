package com.trofimenko.cloud.client;

import com.trofimenko.cloud.common.AbstractMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;

    //обертка для работы с сетью
public class Network {
    private static Socket socket;
    private static ObjectEncoderOutputStream out;
    private static ObjectDecoderInputStream in;

    //создаем коннект и потоки нетти
    public static void start() {
        try {
            socket = new Socket("localhost", 8189);
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream(), 50 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //просто стоп
    public static void stop() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //читаем прилитевший объект с сервака (эта операция блокирующая так как IO)
    public static AbstractMessage readObject() throws ClassNotFoundException, IOException {
        //читаем объект из входящего потока
        Object obj = in.readObject();
        //кастуем до AbstractMessage (это наш класс прослойка) и возвращаем его
        return (AbstractMessage) obj;
    }

    //посылаем в сторону сервака запрос с именем файла
    public static boolean sendMsg(AbstractMessage msg) {
        try {
            //это когда мы читаем из объекта AbstractMessage (msg) в исходящий поток. тоесть передаем исходящее сообщение в поток. отправляем его
            //если отправка успешна то вернется true
            out.writeObject(msg);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}