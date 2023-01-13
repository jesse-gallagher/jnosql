/*
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
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
 */
package org.eclipse.jnosql.mapping.repository.returns;

import jakarta.nosql.DynamicQueryException;
import jakarta.nosql.Page;
import org.eclipse.jnosql.mapping.repository.DynamicReturn;

public class PageRepositoryReturn extends AbstractRepositoryReturn {

    public PageRepositoryReturn() {
        super(Page.class);
    }

    @Override
    public <T> Object convert(DynamicReturn<T> dynamicReturn) {
        throw new DynamicQueryException("There is not pagination at the method: " + dynamicReturn.getMethod());
    }

    @Override
    public <T> Object convertPageable(DynamicReturn<T> dynamicReturn) {
        return dynamicReturn.getPage();
    }
}
