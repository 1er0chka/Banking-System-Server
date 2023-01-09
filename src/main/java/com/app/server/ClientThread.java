package com.app.server;

import com.app.controller.ApplicationController;
import com.app.cryptography.RSA;
import com.app.mapper.JSON;
import com.app.model.*;
import com.app.service.AccountService;
import com.app.service.CardService;
import com.app.service.OperationService;
import com.app.service.UserService;

import java.io.*;
import java.net.Socket;
import java.security.*;

public class ClientThread extends Thread {
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private Request request;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    private final UserService userService = new UserService();
    private final AccountService accountService = new AccountService();
    private final CardService cardService = new CardService();
    private final OperationService operationService = new OperationService();

    // Подключение клиента
    public ClientThread(Socket clientSocket) {
        try {
            request = new Request();
            this.clientSocket = clientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            ApplicationController.addError("Не удалось создать поток для клиента");
        }
    }

    // Отправка данных
    private void send(Request request) throws IOException {
        String data = (new JSON<Request>().toJson(request));
        out.writeUTF(RSA.encoding(data, publicKey));
    }

    // Получение данных
    public Request get() throws IOException {
        String data = in.readUTF();
        return (new JSON<Request>()).fromJson(RSA.decoding(data, privateKey), Request.class);
    }

    // Поток клиента
    @Override
    public void run() {
        try {
            // Генерация ключей RSA
            createKeys();
            while (clientSocket.isConnected()) {
                // Получение данных от клиента
                request = get();
                // Определение типа операции
                Request.RequestType requestType = request.getRequestType();
                switch (requestType) {
                    case Registration: {
                        try {
                            ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": регистрация пользователя");
                            // Чтение данных пользователя
                            User user = (new JSON<User>()).fromJson(request.getRequestMessage(), User.class);
                            // Добавление пользователя
                            request.setRequestMessage(userService.add(user));
                            if (request.getRequestMessage().equals("true")) {
                                ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": пользователь зарегистрирован");
                            } else {
                                ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": пользователь не зарегистрирован");
                            }
                            // Отправка ответа клиенту
                            send(request);
                        } catch (Exception e) {
                            ApplicationController.addError("Ошибка регистрации");
                        }
                    }
                    break;
                    case LogIn: {
                        try {
                            ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": авторизация пользователя");
                            // Чтение данных пользователя
                            User user = (new JSON<User>()).fromJson(request.getRequestMessage(), User.class);
                            // Авторизация
                            user = userService.authorization(user);
                            if (user == null || user.getRole().equals("block")) {
                                request.setRequestMessage("false");
                                ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": пользователь не авторизирован");
                            } else {
                                request.setRequestMessage((new JSON<User>()).toJson(user));
                                ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": пользователь авторизирован");
                            }
                            // Отправка ответа клиенту
                            send(request);
                            // Если авторизация успешна - отправить данные пользователя
                            if (!request.getRequestMessage().equals("false")) {
                                refreshUser(user.getId());
                            }
                        } catch (Exception e) {
                            ApplicationController.addError("Ошибка авторизации");
                        }
                    }
                    break;
                    case LogOut: {
                        ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": пользователь вышел из аккаунта");
                    }
                    break;
                    case UpdateUser: {
                        ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": изменение данных пользователя");
                        // Чтение данных пользователя
                        User user = (new JSON<User>()).fromJson(request.getRequestMessage(), User.class);
                        // Изменение пользователя
                        userService.update(user);
                        // Отправка данных пользователя
                        refreshUser(user.getId());
                        ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": данные пользователя изменены");
                    }
                    break;
                    case GetAllUser: {
                        ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": пользователь запросил данные всех аккаунтов");
                        // Чтение данных всех пользователей
                        request.setRequestMessage((new JSON<User>()).toJson(userService.getAll()));
                        // Отправка данных клиенту
                        send(request);
                    }
                    break;
                    case CreateAccount: {
                        ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": добавление банковского счета");
                        // Чтение данных счета
                        Account account = (new JSON<Account>()).fromJson(request.getRequestMessage(), Account.class);
                        // Создание банковского счета
                        request.setRequestMessage(accountService.add(account));
                        // Отправка ответа клиенту
                        send(request);
                        if (request.getRequestMessage().equals("true")) {
                            ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": банковский счет добавлен");
                            // Отправка данных пользователя
                            refreshUser(account.getUser().getId());
                        } else {
                            ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": банковский счет не добавлен");
                        }
                    }
                    break;
                    case UpdateAccount: {
                        ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": редактирование банковского счета");
                        // Чтение данных счета
                        Account account = (new JSON<Account>()).fromJson(request.getRequestMessage(), Account.class);
                        // Редактирование счета
                        request.setRequestMessage(accountService.update(account));
                        // Отправка ответа клиенту
                        send(request);
                        if (request.getRequestMessage().equals("true")) {
                            ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": банковский счет изменен");
                            // Отправка данных пользователя
                            refreshUser(account.getUser().getId());
                        } else {
                            ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": банковский счет не изменен");
                        }
                    }
                    break;
                    case DeleteAccount: {
                        ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": удаление банковского счета");
                        // Чтение данных счета
                        Account account = (new JSON<Account>()).fromJson(request.getRequestMessage(), Account.class);
                        int userId = account.getUser().getId();
                        // Удаление счета
                        accountService.delete(account);
                        ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": банковский счет удален");
                        // Отправка данных пользователя
                        refreshUser(userId);
                    }
                    break;
                    case CreateCard: {
                        ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": добавление банковской карты");
                        // Чтение данных карты
                        Card card = (new JSON<Card>()).fromJson(request.getRequestMessage(), Card.class);
                        // Добавление карты
                        request.setRequestMessage(cardService.add(card));
                        // Отправка ответа
                        send(request);
                        if (request.getRequestMessage().equals("true")) {
                            ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": банковская карта добавлена");
                            // Отправка данных пользователя
                            refreshUser(card.getAccount().getUser().getId());
                        } else {
                            ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": банковская карта не добавлена");
                        }
                    }
                    break;
                    case DeleteCard: {
                        ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": удаление банковской карты");
                        // Чтение данных карты
                        Card card = (new JSON<Card>()).fromJson(request.getRequestMessage(), Card.class);
                        // Удаление карты
                        cardService.delete(card);
                        ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": банковская карта удалена");
                        // Отправка данных пользователя
                        refreshUser(card.getAccount().getUser().getId());
                    }
                    break;
                    case TransferCard: {
                        ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": выполнение банковского перевода");
                        // Чтение данных карты отправителя
                        Card senderCard = (new JSON<Card>()).fromJson(request.getRequestMessage(), Card.class);
                        // Поиск карты отправителя в бд
                        senderCard = cardService.findSender(senderCard);
                        if (senderCard == null) {
                            ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": банковский перевод отклонен");
                            request.setRequestMessage("false");
                            send(request);
                            break;
                        }
                        // Отправка ответа клиенту
                        send(request);
                        // Получение данных от клиента
                        request = get();
                        // Чтение данных карты получателя
                        Card recipientCard = (new JSON<Card>()).fromJson(request.getRequestMessage(), Card.class);
                        // Поиск карты получателя в бд
                        recipientCard = cardService.findRecipient(recipientCard);
                        if (recipientCard == null) {
                            ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": банковский перевод отклонен");
                            request.setRequestMessage("false");
                            send(request);
                            break;
                        }
                        // Отправка ответа клиенту
                        send(request);
                        // Получение данных от клиента
                        request = get();
                        // Чтение данных операции
                        Operation operation = (new JSON<Operation>()).fromJson(request.getRequestMessage(), Operation.class);
                        // Если достаточно средств
                        if (senderCard.getAccount().getAmount() >= operation.getSum()) {
                            // Снятие средств со счета отправителя
                            Account senderAccount = accountService.get(senderCard.getAccount().getId());
                            senderAccount.setAmount(senderAccount.getAmount() - operation.getSum());
                            accountService.updateAmount(senderAccount);
                            // Зачисление средств на счет получателя
                            Account recipientAccount = accountService.get(recipientCard.getAccount().getId());
                            recipientAccount.setAmount(recipientAccount.getAmount() + operation.getSum());
                            accountService.updateAmount(recipientAccount);
                        } else {
                            ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": банковский перевод отклонен");
                            request.setRequestMessage("false");
                            send(request);
                            break;
                        }
                        // Создание записи об операции снятия средств
                        operation.setCard(senderCard);
                        operationService.add(operation);
                        // Создание записи об операции зачисления средств
                        operation.setCard(recipientCard);
                        operation.setName("Пополнение счета с карты");
                        operationService.add(operation);
                        ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": банковский перевод выполнен");
                        // Отправка ответа клиенту
                        request.setRequestMessage("true");
                        send(request);
                        // Получение данных от клиента
                        request = get();
                        // Чтение данных пользователя
                        User user = (new JSON<User>()).fromJson(request.getRequestMessage(), User.class);
                        // Отправка данных пользователя
                        refreshUser(user.getId());
                    }
                    break;
                    case TransferAccount: {
                        ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": выполнение банковского перевода");
                        // Чтение данных счета отправителя
                        Account senderAccount = (new JSON<Account>()).fromJson(request.getRequestMessage(), Account.class);
                        // Поиск счета отправителя в бд
                        senderAccount = accountService.getByNumber(senderAccount.getAccountNumber());
                        if (senderAccount == null) {
                            ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": банковский перевод отклонен");
                            request.setRequestMessage("false");
                            send(request);
                            break;
                        }
                        // Отправка ответа клиенту
                        send(request);
                        // Получение данных от клиента
                        request = get();
                        // Чтение данных счета получателя
                        Account recipientAccount = (new JSON<Account>()).fromJson(request.getRequestMessage(), Account.class);
                        // Поиск счета получателя в бд
                        recipientAccount = accountService.getByNumber(recipientAccount.getAccountNumber());
                        if (recipientAccount == null) {
                            ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": банковский перевод отклонен");
                            request.setRequestMessage("false");
                            send(request);
                            break;
                        }
                        // Отправка ответа клиенту
                        send(request);
                        // Получение данных от клиента
                        request = get();
                        // Чтение данных операции
                        Operation operation = (new JSON<Operation>()).fromJson(request.getRequestMessage(), Operation.class);
                        // Если средств достаточно
                        if (senderAccount.getAmount() >= operation.getSum()) {
                            // Снятие средств со счета отправителя
                            senderAccount.setAmount(senderAccount.getAmount() - operation.getSum());
                            accountService.updateAmount(senderAccount);
                            // Зачисление средств на счет получателя
                            recipientAccount.setAmount(recipientAccount.getAmount() + operation.getSum());
                            accountService.updateAmount(recipientAccount);
                        } else {
                            ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": банковский перевод отклонен");
                            request.setRequestMessage("false");
                            send(request);
                            break;
                        }
                        ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": банковский перевод выполнен");
                        // Отправка ответа клиенту
                        request.setRequestMessage("true");
                        send(request);
                        // Получение данных от клиента
                        request = get();
                        // Чтение данных пользователя
                        User user = (new JSON<User>()).fromJson(request.getRequestMessage(), User.class);
                        // Отправка данных пользователя
                        refreshUser(user.getId());
                    }
                    break;
                    case Payment: {
                        ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": выполнение банковского платежа");
                        // Чтение данных карты
                        Card card = (new JSON<Card>()).fromJson(request.getRequestMessage(), Card.class);
                        // Поиск карты в бд
                        card = cardService.findSender(card);
                        if (card == null) {
                            ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": банковский платеж отклонен");
                            request.setRequestMessage("false");
                            send(request);
                            break;
                        }
                        // Отправка ответа клиенту
                        send(request);
                        // Получение данных от клиента
                        request = get();
                        // Чтение данных операции
                        Operation operation = (new JSON<Operation>()).fromJson(request.getRequestMessage(), Operation.class);
                        // Если средств достаточно
                        if (card.getAccount().getAmount() >= operation.getSum()) {
                            // Снятие средств со счета
                            Account account = accountService.get(card.getAccount().getId());
                            account.setAmount(account.getAmount() - operation.getSum());
                            accountService.updateAmount(account);
                        } else {
                            ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": банковский платеж отклонен");
                            request.setRequestMessage("false");
                            send(request);
                            break;
                        }
                        // Создание записи об операции
                        operation.setCard(card);
                        operationService.add(operation);
                        ApplicationController.addAudit(String.valueOf(clientSocket.getInetAddress()).substring(1) + ": банковский платеж выполнен");
                        // Отправка ответа клиенту
                        request.setRequestMessage("true");
                        send(request);
                        // Получение данных от клиента
                        request = get();
                        // Чтение данных пользователя
                        User user = (new JSON<User>()).fromJson(request.getRequestMessage(), User.class);
                        //// Отправка данных пользователя
                        refreshUser(user.getId());
                    }
                    break;
                    case Exit: {
                        // Отключение клиента
                        Server.removeSocket(clientSocket);
                        // Закрытие сокета
                        clientSocket.close();
                    }
                    default:
                        break;
                }
            }
        } catch (IOException | NoSuchAlgorithmException ignored) {
        }
    }

    // Отравка полных данных пользователя
    private void refreshUser(int id) {
        try {
            User user = userService.get(id);
            send(new Request(null, (new JSON<User>()).toJson(user)));
            get();
            send(new Request(null, (new JSON<Account>()).toJson(user.getAccounts())));
            get();
            for (Account account : user.getAccounts()) {
                send(new Request(null, (new JSON<Card>()).toJson(account.getCards())));
                get();
                for (Card card : account.getCards()) {
                    send(new Request(null, (new JSON<Operation>()).toJson(card.getOperations())));
                    get();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Генерация ключей
    private void createKeys() {
        try {
            KeyPair kp = RSA.getKeys();
            privateKey = kp.getPrivate(); // ключ для расшифровки сообщений
            (new ObjectOutputStream(out)).writeObject(kp.getPublic()); // отправляем клиенту ключ, которым он будет зашифровывать сообщения
            publicKey = (PublicKey) (new ObjectInputStream(in)).readObject();
        } catch (Exception e) {
            ApplicationController.addError("Ошибка получения ключей");
        }
    }
}
