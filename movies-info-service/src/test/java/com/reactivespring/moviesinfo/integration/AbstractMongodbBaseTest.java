package com.reactivespring.moviesinfo.integration;

import org.testcontainers.containers.MongoDBContainer;

public abstract class AbstractMongodbBaseTest {
    protected static final MongoDBContainer MONGO_DB_CONTAINER;
    private static final String MONGO_IMAGE = "mongo:latest";

    static {
        MONGO_DB_CONTAINER = new MongoDBContainer(MONGO_IMAGE);
        MONGO_DB_CONTAINER.start();
        System.setProperty("spring.data.mongodb.uri", MONGO_DB_CONTAINER.getReplicaSetUrl());
    }
}
