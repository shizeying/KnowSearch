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

package com.didi.arius.gateway.elasticsearch.client.request.cat;

import com.didi.arius.gateway.elasticsearch.client.response.cat.ESCatResponse;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.client.ElasticsearchClient;

public class ESCatRequestBuilder extends ActionRequestBuilder<ESCatRequest, ESCatResponse, ESCatRequestBuilder> {
    public ESCatRequestBuilder(ElasticsearchClient client, ESCatAction action) {
        super(client, action, new ESCatRequest());
    }

    public ESCatRequestBuilder setUri(String uri) {
        this.request.setUri(uri);
        return this;
    }

    public ESCatRequestBuilder addParam(String key, String value) {
        this.request.addParam(key, value);
        return this;
    }

    public ESCatRequestBuilder removeParam(String key) {
        this.request.removeParam(key);
        return this;
    }

    public ESCatRequestBuilder setClazz(Class clazz) {
        this.request.setClazz(clazz);
        return this;
    }
}
