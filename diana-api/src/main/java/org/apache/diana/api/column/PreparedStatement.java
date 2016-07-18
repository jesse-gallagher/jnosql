package org.apache.diana.api.column;


import org.apache.diana.api.ExecuteAsyncQueryException;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

public interface PreparedStatement extends AutoCloseable {

    List<ColumnEntity> executeQuery();

    void executeQueryAsync(Consumer<List<ColumnEntity>> callBack) throws ExecuteAsyncQueryException;

    <T extends Serializable> PreparedStatement bind(T... values);
}