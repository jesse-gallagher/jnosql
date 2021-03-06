/*
 *  Copyright (c) 2019 Otávio Santana and others
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

package org.eclipse.jnosql.artemis.configuration;

import jakarta.nosql.Settings;
import org.eclipse.jnosql.artemis.CDIExtension;
import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(CDIExtension.class)
class SettingsConverterTest {


    @Inject
    private Config config;

    @Test
    public void shouldLoadEmptySettings() {
        System.setProperty("database", "prefix");
        final Settings settings = config.getValue("database", Settings.class);
        Assertions.assertNotNull(settings);
        assertEquals(0, settings.size());
        System.clearProperty("database");
    }


    @Test
    public void shouldSettings() {
        final String prefix = UUID.randomUUID().toString();
        System.setProperty(prefix, prefix);
        System.setProperty(prefix + ".settings.key", "value");
        System.setProperty(prefix + ".settings.key2", "value2");
        final Settings settings = config.getValue(prefix, Settings.class);
        Assertions.assertNotNull(settings);
        assertEquals(2, settings.size());
        assertEquals(settings.get("key").get(), "value");
        assertEquals(settings.get("key2").get(), "value2");


        System.clearProperty(prefix);
        System.clearProperty(prefix + ".settings.key");
        System.clearProperty(prefix + ".settings.key2");
    }
}