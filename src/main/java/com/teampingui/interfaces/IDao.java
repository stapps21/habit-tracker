package com.teampingui.interfaces;

import javafx.collections.ObservableList;

import java.util.Optional;

public interface IDao<T> {

    Optional<T> get(long id);

    ObservableList<T> getAll();

    int insert(T t) throws Exception;

    void update(int index, T t);

    void delete(T t);

    int indexOf(T t);
}
