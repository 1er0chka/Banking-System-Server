package com.app.service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public interface Service<Entity> {
    // Получение элемента по id
    Entity get(int id);

    // Получение всех элементов
    ArrayList<Entity> getAll();

    // Создание нового элемента
    String add(Entity entity) throws NoSuchAlgorithmException;

    // Обновление элемента
    String update(Entity entity);

    // Удаление элемента
    void delete(Entity entity);
}
