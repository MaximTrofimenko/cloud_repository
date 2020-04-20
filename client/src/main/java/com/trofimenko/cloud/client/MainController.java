package com.trofimenko.cloud.client;

import com.trofimenko.cloud.common.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    //текстовое поле
    @FXML
    TextField tfFileName;
    @FXML
    TextField zfFileName;
    //список файлов
    @FXML
    ListView<String> filesList;
    @FXML
    ListView<String> serverFilesList;
    @FXML
    TextField dFileName;
    @FXML
    TextField ddFileName;
    @FXML
    TextField cFileName;


    //при запуске формы срабатывает метод инициалайз
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Network.start();
        Thread t = new Thread(() -> {
            try {
                //слушаем все что нам скажет сервак, все это в отдельм треде,
                while (true) {
                    AbstractMessage am = Network.readObject();
                    //сохраняем файл на клиента
                    if (am instanceof FileMessage) {
                        //кастуем входящий объект(сообщение)
                        FileMessage fm = (FileMessage) am;
                        /*сохранияем все в клиентское хранилище - прописываем путь, дергаем из входяжего объекта
                        имя файла, массив где хранятся нужные данные и говорим что если там такой файл был то мы его перезапишем
                        */
                        Files.write(Paths.get("client_storage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                        //апдейтим список локальных файлов
                        refreshLocalFilesList();
                    }
                    //обновляем список файлов на серваке
                    if (am instanceof FileListMessage){
                        FileListMessage flm = (FileListMessage) am;
                        refreshLocalServerFilesList(flm);
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                Network.stop();
            }
        });
        t.setDaemon(true);
        t.start();
        refreshLocalFilesList();
        Network.sendMsg(new FileRequestInitial());
    }
    /*команда на кнопках*/
    //если хотим скачать файл С сервака. клиент должен послать серверу Request
    public void pressOnDownloadBtn(ActionEvent actionEvent) {
        //проверяем что имя файла указано длиннее чем ноль
        if (tfFileName.getLength() > 0) {
            //посылаем в сторону сервака запрос с именем файла
            Network.sendMsg(new FileRequest(tfFileName.getText()));
            //чистим имя файла
            tfFileName.clear();
        }
    }
    //выгружаем файл НА сервак
    public void pressOnUnloadBtn(ActionEvent actionEvent) throws IOException {
        //проверяем что имя файла указано длиннее чем ноль
        if (zfFileName.getLength() > 0){
            //посылаем в сторону сервака объект(туда уже загружены байты, так как конструктор это подразумевает)
            //и если результат true то чистим поле и обновляем лист файлов
            Network.sendMsg(new FileMessage(Paths.get("client_storage/" + zfFileName.getText())));
            zfFileName.clear();
        }
    }
    //удаляем файл на клиенте
    public void pressOnDelClientBtn(ActionEvent actionEvent){
        new File("client_storage/" + dFileName.getText()).delete();
        dFileName.clear();
        refreshLocalFilesList();
    }
    //удаляем файл на сервере
    public void pressOnDelServerBtn(ActionEvent actionEvent){
        Network.sendMsg(new FileDeleteMessage(ddFileName.getText()));
        ddFileName.clear();

    }
    //создаем файл на клиенте
    public void pressOnCreateBtn(ActionEvent actionEvent)  {
        try {
            new File("client_storage/" + cFileName.getText()).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cFileName.clear();
        refreshLocalFilesList();
    }



/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //апдейтим список локальных файлов
    public void refreshLocalFilesList() {
        updateUI(() -> {
            try {
                //когда нам файл прилетел то мы чистим список локальных файлов и
                filesList.getItems().clear();
               //формируем список файлов повторно
                Files.list(Paths.get("client_storage")).map(p -> p.getFileName().toString()).forEach(o -> filesList.getItems().add(o));
                } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    //мы не можем менять интерфейс не из потока JavaFx
    //это метод написан для упрощения метода refreshLocalFilesList
    public static void updateUI(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
    }

    //тут обновить список файлов на серваке
    public void refreshLocalServerFilesList(FileListMessage flm){
        updateUI(() -> {
               //когда нам файл прилител то мы чистим список локальных файлов
                serverFilesList.getItems().clear();
                //перегоняем список имен файлов в массив на java fx
                for (String o : flm.getArr()) {
                    serverFilesList.getItems().add(o);
                }
        });
    }
}

