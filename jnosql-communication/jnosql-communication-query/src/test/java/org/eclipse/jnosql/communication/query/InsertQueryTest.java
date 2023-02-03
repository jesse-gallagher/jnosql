/*
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *  You may elect to redistribute this code under either of these licenses.
 *  Contributors:
 *  Otavio Santana
 */

package org.eclipse.jnosql.communication.query;

import org.eclipse.jnosql.communication.QueryException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class InsertQueryTest {

    @ParameterizedTest
    @ArgumentsSource(InsertQueryArgumentProvider.class)
    public void shouldExecuteQuery(String query) {
        testQuery(query);
    }

    @Test
    public void shouldIgnoreComments() {
        testQuery("//ignore this line \n insert Person (name = \"Ada Lovelace\")");
    }

    @ParameterizedTest
    @ArgumentsSource(WrongInsertQueryArgumentProvider.class)
    public void shouldNotExecute(String query) {
        Assertions.assertThrows(QueryException.class, () -> testQuery(query));
    }

    @Test
    public void shouldCreateFromStaticMethod() {
        InsertQuery query = InsertQuery.parse("insert Person (name = \"Ada Lovelace\")");
        Assertions.assertNotNull(query);
    }

    @Test
    public void shouldEquals(){
        String text = "insert Person (name = \"Ada Lovelace\")";
        Assertions.assertEquals(InsertQuery.parse(text), InsertQuery.parse(text));
    }

    @Test
    public void shouldHashCode() {
        String text = "insert Person (name = \"Ada Lovelace\")";
        Assertions.assertEquals(InsertQuery.parse(text).hashCode(), InsertQuery.parse(text).hashCode());
    }

    private void testQuery(String query) {
        CharStream stream = CharStreams.fromString(query);
        QueryLexer lexer = new QueryLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        QueryParser parser = new QueryParser(tokens);
        lexer.removeErrorListeners();
        parser.removeErrorListeners();
        lexer.addErrorListener(QueryErrorListener.INSTANCE);
        parser.addErrorListener(QueryErrorListener.INSTANCE);

        ParseTree tree = parser.insert();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new QueryBaseListener(), tree);


    }


}
