// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.sample;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosAsyncDatabase;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.implementation.Utils;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosItemIdentity;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.models.FeedResponse;
import com.azure.cosmos.models.ThroughputProperties;
import com.azure.cosmos.sample.common.AccountSettings;
import com.azure.cosmos.sample.common.Item;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadManyItems {

    private static CosmosAsyncClient client;

    private final String databaseName = "ReadManyItemsDB";
    private final String containerName = "CosmosContainer";

    private CosmosAsyncDatabase database;
    private CosmosAsyncContainer container;

    Queue<String> docIDs = new ConcurrentLinkedQueue<String>();
    AtomicInteger exceptionCount = new AtomicInteger(0);
    AtomicLong insertCount = new AtomicLong(0);
    AtomicInteger recordCount = new AtomicInteger(0);
    AtomicInteger verifyCount = new AtomicInteger(0);
    AtomicLong totalLatency = new AtomicLong(0);
    AtomicLong totalReadLatency = new AtomicLong(0);
    AtomicLong totalEnhancedReadLatency = new AtomicLong(0);
    AtomicReference<Double> totalRequestCharges = new AtomicReference<>((double) 0);
    AtomicReference<Double> totalEnhancedRequestCharges = new AtomicReference<>((double) 0);
    private static AtomicInteger number_docs_inserted = new AtomicInteger(0);
    private static AtomicInteger request_count = new AtomicInteger(0);

    public static final int PROVISIONED_RUS = 15000;
    public static final int NUMBER_OF_DOCS = 1000;
    public static final int NUMBER_OF_DOCS_PER_THREAD = 100;
    public ArrayList<JsonNode> docs;

    private final static Logger logger = LoggerFactory.getLogger(ReadManyItems.class);

    public void close() {
        client.close();
    }

    /**
     * Demo readMany method...
     * 
     * @param args command line args.
     */
    // <Main>
    public static void main(String[] args) {
        ReadManyItems p = new ReadManyItems();

        try {
            p.readManyItemsDemo();
            logger.info("Demo complete, please hold while resources are released");
        } catch (Exception e) {
            logger.info("Cosmos getStarted failed with: " + e);
        } finally {
            logger.info("Closing the client");
            p.close();
        }
        System.exit(0);
    }
    // </Main>

    private void readManyItemsDemo() throws Exception {
        logger.info("Using Azure Cosmos DB endpoint: " + AccountSettings.HOST);
        docs = generateDocs(NUMBER_OF_DOCS);
        client = new CosmosClientBuilder()
                .endpoint(AccountSettings.HOST)
                .key(AccountSettings.MASTER_KEY)
                // Setting the preferred location to Cosmos DB Account region
                // UK South is just an example. User should set preferred location to the Cosmos
                // DB region closest to the application
                .preferredRegions(Collections.singletonList("UK South"))
                .consistencyLevel(ConsistencyLevel.SESSION)
                .contentResponseOnWriteEnabled(true)
                .directMode()
                .buildAsyncClient();

        Mono<Void> databaseContainerIfNotExist = client.createDatabaseIfNotExists(databaseName)
                .flatMap(databaseResponse -> {
                    database = client.getDatabase(databaseResponse.getProperties().getId());
                    logger.info("\n\n\n\nCreated database ReadManyItemsDB.\n\n\n\n");
                    CosmosContainerProperties containerProperties = new CosmosContainerProperties(containerName, "/id");
                    ThroughputProperties throughputProperties = ThroughputProperties.createManualThroughput(PROVISIONED_RUS);
                    return database.createContainerIfNotExists(containerProperties, throughputProperties);
                }).flatMap(containerResponse -> {
                    container = database.getContainer(containerResponse.getProperties().getId());
                    logger.info("\n\n\n\nCreated container Container.\n\n\n\n");
                    return Mono.empty();
                });

        logger.info("Creating database and container asynchronously...");
        databaseContainerIfNotExist.block();

        createManyItems(docs);

        logger.info("Reading many items....");
        readManyItems();
        logger.info("Reading many items (enhanced using readMany method)");
        readManyItemsEnhanced();

        logger.info("Total latency with standard multi-threading: " + totalReadLatency);
        logger.info("Total latency using readMany method: " + totalEnhancedReadLatency);
        logger.info("Total request charges with standard multi-threading: " + totalRequestCharges);
        logger.info("Total request charges using readMany method: " + totalEnhancedRequestCharges);
    }

    private void createManyItems(ArrayList<JsonNode> docs) throws Exception {
        final long startTime = System.currentTimeMillis();
        Flux.fromIterable(docs).flatMap(doc -> container.createItem(doc))
                .flatMap(itemResponse -> {
                    if (itemResponse.getStatusCode() == 201) {
                        number_docs_inserted.getAndIncrement();
                    } else
                        logger.info("WARNING insert status code {} != 201" + itemResponse.getStatusCode());
                    request_count.incrementAndGet();
                    return Mono.empty();
                }).subscribe(); // ...Subscribing to the publisher triggers stream execution.

        logger.info("Doing other things until async doc inserts complete...");
        while (request_count.get() < NUMBER_OF_DOCS) {
        }
        if (request_count.get() == NUMBER_OF_DOCS) {
            request_count.set(0);
            final long endTime = System.currentTimeMillis();
            final long duration = (endTime - startTime);
            this.totalLatency.getAndAdd(duration);
        }

    }

    private void readManyItems() throws InterruptedException {
        // collect the ids that were generated when writing the data.
        List<String> list = new ArrayList<String>();
        for (final JsonNode doc : docs) {
            list.add(doc.get("id").asText());
        }

        final long startTime = System.currentTimeMillis();
        Flux.fromIterable(list)
                .flatMap(id -> container.readItem(id, new PartitionKey(id), Item.class))
                .flatMap(itemResponse -> {
                    if (itemResponse.getStatusCode() == 200) {
                        double requestCharge = itemResponse.getRequestCharge();
                        BinaryOperator<Double> add = (u, v) -> u + v;
                        totalRequestCharges.getAndAccumulate(requestCharge, add);

                    } else
                        logger.info("WARNING insert status code {} != 200" + itemResponse.getStatusCode());
                    request_count.getAndIncrement();
                    return Mono.empty();
                }).subscribe();

        logger.info("Waiting while subscribed async operation completes all threads...");
        while (request_count.get() < NUMBER_OF_DOCS) {
            // looping while subscribed async operation completes all threads
        }
        if (request_count.get() == NUMBER_OF_DOCS) {
            request_count.set(0);
            final long endTime = System.currentTimeMillis();
            final long duration = (endTime - startTime);
            totalReadLatency.getAndAdd(duration);
        }

    }

    private void readManyItemsEnhanced() throws InterruptedException {
        // collect the ids that were generated when writing the data.
        List<String> list = new ArrayList<String>();
        for (final JsonNode doc : docs) {
            list.add(doc.get("id").asText());
        }
        List<List<String>> lists = ListUtils.partition(list, NUMBER_OF_DOCS_PER_THREAD);

        final long startTime = System.currentTimeMillis();
        Flux.fromIterable(lists).flatMap(x -> {

            List<CosmosItemIdentity> pairList = new ArrayList<>();

            // add point reads in this thread as a list to be sent to Cosmos DB
            for (final String id : x) {
                // increment request count here so that total requests will equal total docs
                request_count.getAndIncrement();
                pairList.add(new CosmosItemIdentity(new PartitionKey(String.valueOf(id)), String.valueOf(id)));
            }

            // instead of reading sequentially, send CosmosItem id and partition key tuple of items to be read
            Mono<FeedResponse<Item>> documentFeedResponse = container.readMany(pairList, Item.class);                    
            double requestCharge = documentFeedResponse.block().getRequestCharge();
            BinaryOperator<Double> add = (u, v) -> u + v;
            totalEnhancedRequestCharges.getAndAccumulate(requestCharge, add);
            return documentFeedResponse;
        })
                .subscribe();

        logger.info("Waiting while subscribed async operation completes all threads...");
        while (request_count.get() < NUMBER_OF_DOCS) {
            // looping while subscribed async operation completes all threads
        }
        if (request_count.get() == NUMBER_OF_DOCS) {
            request_count.set(0);
            final long endTime = System.currentTimeMillis();
            final long duration = (endTime - startTime);
            totalEnhancedReadLatency.getAndAdd(duration);
        }
    }

    public static ArrayList<JsonNode> generateDocs(int N) {
        ArrayList<JsonNode> docs = new ArrayList<JsonNode>();
        ObjectMapper mapper = Utils.getSimpleObjectMapper();

        try {
            for (int i = 1; i <= N; i++) {
                docs.add(mapper.readTree(
                        "{" +
                                "\"id\": " +
                                "\"" + UUID.randomUUID().toString() + "\"" +
                                "}"));
            }
        } catch (Exception err) {
            logger.error("Failed generating documents: ", err);
        }

        return docs;
    }
}
