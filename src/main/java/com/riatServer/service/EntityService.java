package com.riatServer.service;
import com.riatServer.exception.ServiceException;

import java.io.IOException;
import java.util.List;
/**
 *
 * @author Atai
 * @param <T>
 * @param <S>
 */
public interface EntityService<T,S> {

    List<T> getAll();

    T getById(S id);

    T save(T t);

    T create(T t);

    void delete(S id) throws IOException, ServiceException;

}