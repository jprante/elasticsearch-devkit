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

module org.xbib.elasticsearch.analysis.common.test {

    exports org.elasticsearch.analysis.common.test;

    requires junit;
    requires hamcrest.all;
    requires org.xbib.elasticsearch.analysis.common;
    requires org.xbib.elasticsearch.testframework;
    requires org.xbib.elasticsearch.server;
    requires org.xbib.elasticsearch.lucene;
    requires org.xbib.elasticsearch.lucene.testframework;
    requires org.xbib.elasticsearch.log4j;
    requires org.xbib.elasticsearch.randomizedtesting;
    requires org.xbib.elasticsearch.randomizedtesting.junit.ant;

    opens org.elasticsearch.analysis.common.test;
    //opens restapispec.test.analysis;
    //opens restapispec.test.indices.analyze;
    //opens restapispec.test.search.query;
    //opens restapispec.test.search.suggest;
    //opens restapispec.test.termvectors;
}
