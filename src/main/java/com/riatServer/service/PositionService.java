package com.riatServer.service;

import com.riatServer.domain.Position;

import java.util.List;

public interface PositionService {
    void delete(Position position);

    Position save(Position position);

    List<Position> getAll();

    List<Position> getAll(String value);

    Position create(Position position);
}
