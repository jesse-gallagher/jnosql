/*
 *  Copyright (c) 2023 Contributors to the Eclipse Foundation
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
package org.eclipse.jnosql.mapping.reflection;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.PageableRepository;
import jakarta.data.repository.Repository;
import jakarta.nosql.Entity;
import org.eclipse.jnosql.mapping.Embeddable;
import org.eclipse.jnosql.mapping.metadata.ClassScanner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toUnmodifiableSet;

/**
 * Scanner classes that will load entities with both Entity and Embeddable
 * annotations and repositories: interfaces that extend DataRepository
 * and has the Repository annotation.
 */
enum ClassGraphClassScanner implements ClassScanner {

    INSTANCE;

    private final Set<Class<?>> entities;
    private final Set<Class<?>> repositories;
    private final Set<Class<?>> embeddables;


    ClassGraphClassScanner() {
        entities = new HashSet<>();
        embeddables = new HashSet<>();
        repositories = new HashSet<>();

        Logger logger = Logger.getLogger(ClassGraphClassScanner.class.getName());
        logger.fine("Starting scan class to find entities, embeddable and repositories.");
        try (ScanResult result = new ClassGraph().enableAllInfo().scan()) {
            this.entities.addAll(loadEntities(result));
            embeddables.addAll(loadEmbeddable(result));
            this.repositories.addAll(loadRepositories(result));
        }
        logger.fine(String.format("Finished the class scan with entities %d, embeddables %d and repositories: %d"
                , entities.size(), embeddables.size(), repositories.size()));

    }

     @Override
    public Set<Class<?>> entities() {
        return unmodifiableSet(entities);
    }

  @Override
    public Set<Class<?>> repositories() {
        return unmodifiableSet(repositories);
    }


   @Override
    public Set<Class<?>> embeddables() {
        return unmodifiableSet(embeddables);
    }

    @Override
    public <T extends DataRepository<?, ?>> Set<Class<?>> repositories(Class<T> filter) {
        Objects.requireNonNull(filter, "filter is required");
        return repositories.stream().filter(filter::isAssignableFrom)
                .filter(c -> Arrays.asList(c.getInterfaces()).contains(filter))
                .collect(toUnmodifiableSet());
    }


    @Override
    public Set<Class<?>> repositoriesStandard() {
        return repositories.stream()
                .filter(c -> {
                    List<Class<?>> interfaces = Arrays.asList(c.getInterfaces());
                    return interfaces.contains(CrudRepository.class) || interfaces.contains(PageableRepository.class);
                }).collect(Collectors.toUnmodifiableSet());
    }


    @SuppressWarnings("rawtypes")
    private static List<Class<DataRepository>> loadRepositories(ScanResult scan) {
        return scan.getClassesWithAnnotation(Repository.class)
                .getInterfaces()
                .loadClasses(DataRepository.class)
                .stream().filter(RepositoryFilter.INSTANCE)
                .toList();
    }

    @SuppressWarnings("rawtypes")
    private static List<Class<DataRepository>> loadInvalidRepositories(ScanResult scan) {
        return scan.getClassesWithAnnotation(Repository.class)
                .getInterfaces()
                .loadClasses(DataRepository.class)
                .stream().filter(RepositoryFilter.INSTANCE::isInvalid)
                .toList();
    }

    private static List<Class<?>> loadEmbeddable(ScanResult scan) {
        return scan.getClassesWithAnnotation(Embeddable.class).loadClasses();
    }

    private static List<Class<?>> loadEntities(ScanResult scan) {
        return scan.getClassesWithAnnotation(Entity.class).loadClasses();
    }
}
