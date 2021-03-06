/*
 *
 *  Copyright (c) 2017 Otávio Santana and others
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 *
 */
package org.eclipse.jnosql.diana.column.query;

import jakarta.nosql.QueryException;
import jakarta.nosql.column.ColumnEntity;
import jakarta.nosql.column.ColumnFamilyManagerAsync;
import jakarta.nosql.column.ColumnObserverParser;
import jakarta.nosql.column.ColumnPreparedStatementAsync;
import jakarta.nosql.column.ColumnQueryParserAsync;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * The default implementation {@link ColumnQueryParserAsync}
 */
public final class DefaultColumnQueryParserAsync implements ColumnQueryParserAsync {

    private final DefaultSelectQueryConverter select = new DefaultSelectQueryConverter();
    private final DefaultDeleteQueryConverter delete = new DefaultDeleteQueryConverter();
    private final InsertQueryParser insert = new InsertQueryParser();
    private final UpdateQueryParser update = new UpdateQueryParser();

    @Override
    public void query(String query, ColumnFamilyManagerAsync collectionManager,
                      Consumer<Stream<ColumnEntity>> callBack, ColumnObserverParser observer) {

        validation(query, collectionManager, callBack, observer);
        String command = query.substring(0, 6);
        switch (command) {
            case "select":
                select.queryAsync(query, collectionManager, callBack, observer);
                return;
            case "delete":
                delete.queryAsync(query, collectionManager, callBack, observer);
                return;
            case "insert":
                insert.queryAsync(query, collectionManager, callBack, observer);
                return;
            case "update":
                update.queryAsync(query, collectionManager, callBack, observer);
                return;
            default:
                throw new QueryException(String.format("The command was not recognized at the query %s ", query));
        }
    }

    @Override
    public ColumnPreparedStatementAsync prepare(String query, ColumnFamilyManagerAsync manager, ColumnObserverParser observer) {
        validation(query, manager, observer);
        String command = query.substring(0, 6);

        switch (command) {
            case "select":
                return select.prepareAsync(query, manager, observer);
            case "delete":
                return delete.prepareAsync(query, manager, observer);
            case "insert":
                return insert.prepareAsync(query, manager, observer);
            case "update":
                return update.prepareAsync(query, manager, observer);
            default:
                throw new QueryException(String.format("The command was not recognized at the query %s ", query));
        }
    }

    private void validation(String query, ColumnFamilyManagerAsync manager, ColumnObserverParser observer) {

        requireNonNull(query, "query is required");
        requireNonNull(manager, "manager is required");
        requireNonNull(observer, "observer is required");
        if (query.length() < 6) {
            throw new QueryException(String.format("The query %s is invalid", query));
        }
    }

    private void validation(String query, ColumnFamilyManagerAsync manager,
                            Consumer<Stream<ColumnEntity>> callBack, ColumnObserverParser observer) {

        requireNonNull(query, "query is required");
        requireNonNull(manager, "manager is required");
        requireNonNull(callBack, "callBack is required");
        requireNonNull(observer, "observer is required");
        if (query.length() < 6) {
            throw new QueryException(String.format("The query %s is invalid", query));
        }
    }
}