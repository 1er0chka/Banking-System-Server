package com.app.controller;

import com.app.server.Server;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class ApplicationController implements Initializable {
    @FXML
    private TextArea taErrorLog, taAuditLog, taUsers;
    @FXML
    private Button btnServerState;

    private Server server;
    private static TextArea errorLog, auditLog, users;

    public static void addError(String text) {
        errorLog.setText(errorLog.getText() + text + "\n");
    }

    public static void addAudit(String text) {
        auditLog.setText(auditLog.getText() + text + "\n");
    }

    public static void addUser(String text) {
        users.setText(users.getText() + text + "\n");
    }

    public static void removeUser(String text) {
        users.setText(users.getText().replace(text + "\n", ""));
    }

    // Инициализация элементов окна
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorLog = taErrorLog;
        auditLog = taAuditLog;
        users = taUsers;
    }

    // Изменение состояния сервера
    @FXML
    private void changeServerState() {
        if (btnServerState.getText().equals("Старт")) {
            server = new Server();
            server.start();
            btnServerState.setText("Стоп");
        } else {
            server.stopServer();
            btnServerState.setText("Старт");
        }
    }
}
