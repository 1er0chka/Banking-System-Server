package com.app.server;

import com.app.controller.ApplicationController;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class Server extends Thread {
    private final int PORT = 1234;
    private ServerSocket serverSocket;
    private ClientThread clientHandler;
    private static List<Socket> currentSockets = new ArrayList<>();
    private boolean isStarted;

    // отключение клиента
    public static void removeSocket(Socket socket) {
        currentSockets.remove(socket);
        ApplicationController.addAudit("Клиент " + String.valueOf(socket.getInetAddress()).substring(1) + " отключен");
        ApplicationController.removeUser(String.valueOf(socket.getInetAddress()).substring(1));
    }

    // запуск сервера
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            isStarted = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ApplicationController.addAudit("Сервер запущен");
                    try {
                        while (isStarted) {
                            Socket socket = serverSocket.accept();
                            currentSockets.add(socket);
                            ApplicationController.addAudit("Клиент " + String.valueOf(socket.getInetAddress()).substring(1) + " подключен");
                            ApplicationController.addUser(String.valueOf(socket.getInetAddress()).substring(1));
                            clientHandler = new ClientThread(socket);
                            new Thread(clientHandler).start();
                        }
                    } catch (IOException e) {
                        ApplicationController.addAudit("Сервер остановлен");
                    }
                }
            }).start();
        } catch (IOException e) {
            ApplicationController.addError("Ошибка работы сервера");
        }
    }

    // остановка сервера
    public void stopServer() {
        try {
            isStarted = false;
            for (Socket socket : currentSockets) {
                if (socket.isClosed()) {
                    socket.close();
                }
            }
            serverSocket.close();
        } catch (IOException e) {
            ApplicationController.addError("Не удалось закрыть потоки сервера");
        }
    }
}