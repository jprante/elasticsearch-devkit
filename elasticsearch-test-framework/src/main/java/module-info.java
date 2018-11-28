/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
module org.xbib.elasticsearch.testframework {

    exports org.elasticsearch.testframework;
    exports org.elasticsearch.testframework.bootstrap;
    exports org.elasticsearch.testframework.cli;
    exports org.elasticsearch.testframework.client;
    exports org.elasticsearch.testframework.cluster;
    exports org.elasticsearch.testframework.cluster.routing;
    exports org.elasticsearch.testframework.common.bytes;
    exports org.elasticsearch.testframework.common.inject;
    exports org.elasticsearch.testframework.common.io;
    exports org.elasticsearch.testframework.common.settings;
    exports org.elasticsearch.testframework.common.util;
    exports org.elasticsearch.testframework.discovery;
    exports org.elasticsearch.testframework.disruption;
    exports org.elasticsearch.testframework.engine;
    exports org.elasticsearch.testframework.env;
    exports org.elasticsearch.testframework.gateway;
    exports org.elasticsearch.testframework.hamcrest;
    exports org.elasticsearch.testframework.http;
    exports org.elasticsearch.testframework.index;
    exports org.elasticsearch.testframework.index.alias;
    exports org.elasticsearch.testframework.index.analysis;
    exports org.elasticsearch.testframework.index.engine;
    exports org.elasticsearch.testframework.index.mapper;
    exports org.elasticsearch.testframework.index.reindex;
    exports org.elasticsearch.testframework.index.shard;
    exports org.elasticsearch.testframework.index.store;
    exports org.elasticsearch.testframework.index.translog;
    exports org.elasticsearch.testframework.indices.analysis;
    exports org.elasticsearch.testframework.ingest;
    exports org.elasticsearch.testframework.junit.annotations;
    exports org.elasticsearch.testframework.junit.listeners;
    exports org.elasticsearch.testframework.node;
    exports org.elasticsearch.testframework.plugins;
    exports org.elasticsearch.testframework.repositories;
    exports org.elasticsearch.testframework.repositories.blobstore;
    exports org.elasticsearch.testframework.rest;
    exports org.elasticsearch.testframework.rest.yaml;
    exports org.elasticsearch.testframework.rest.yaml.restspec;
    exports org.elasticsearch.testframework.rest.yaml.section;
    exports org.elasticsearch.testframework.script;
    exports org.elasticsearch.testframework.search;
    exports org.elasticsearch.testframework.search.aggregations;
    exports org.elasticsearch.testframework.search.aggregations.bucket;
    exports org.elasticsearch.testframework.search.aggregations.metrics;
    exports org.elasticsearch.testframework.store;
    exports org.elasticsearch.testframework.tasks;
    exports org.elasticsearch.testframework.threadpool;
    exports org.elasticsearch.testframework.transport;
    exports org.elasticsearch.testframework.transport.nio;
    exports org.elasticsearch.testframework.transport.nio.channel;

    uses org.apache.lucene.codecs.Codec;
    uses org.apache.lucene.codecs.DocValuesFormat;
    uses org.apache.lucene.codecs.PostingsFormat;

    requires jdk.management;
    requires java.logging;
    requires junit;
    requires hamcrest.all;
    requires httpcore;
    requires httpcore.nio;
    requires httpclient;
    requires httpasyncclient;
    requires org.xbib.elasticsearch.server;
    requires org.xbib.elasticsearch.lucene;
    requires org.xbib.elasticsearch.lucene.testframework;
    requires org.xbib.elasticsearch.client.rest;
    requires org.xbib.elasticsearch.hppc;
    requires org.xbib.elasticsearch.log4j;
    requires org.xbib.elasticsearch.jackson;
    requires org.xbib.elasticsearch.joda;
    requires org.xbib.elasticsearch.joptsimple;
    requires org.xbib.elasticsearch.mocksocket;
    requires org.xbib.elasticsearch.securemock;
    requires org.xbib.elasticsearch.securesm;
    requires org.xbib.elasticsearch.randomizedtesting;
}