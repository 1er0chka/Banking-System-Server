package com.app.DAO;

import java.util.ArrayList;

public interface DAO<Entity> {
    // Запись нового элемента в таблицу
    void create(Entity entity);

    // Чтение элемента по id
    Entity read(int index);

    // Чтение всех элементов
    ArrayList<Entity> readAll();

    // Перезапись данных элемента
    void update(Entity entity);

    // Удаление элемента из таблицы
    void delete(Entity entity);
}
