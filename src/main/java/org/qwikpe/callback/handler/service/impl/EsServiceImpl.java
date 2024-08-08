package org.qwikpe.callback.handler.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import org.qwikpe.callback.handler.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class EsServiceImpl implements EsService {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Override
    public <TDocument> GetResponse<TDocument> findById(String index, String id, Class<TDocument> tDocumentClass, List<String> fieldsToInclude) throws IOException {
        GetRequest getRequest = GetRequest.of(builder -> builder.index(index).id(id).sourceIncludes(fieldsToInclude));
        return elasticsearchClient.get(getRequest, tDocumentClass);
    }


    @Override
    public <T> SearchResponse<T> findByFields(List<String> indexList, Map<String, Object> valueMap, Class<T> returnType, List<String> fieldsToInclude) throws IOException {


        SearchRequest searchRequest = SearchRequest.of(builder ->
                builder.index(indexList).query(queryBuilder -> queryBuilder.match(matchBuilder -> {
                    for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                        matchBuilder.field(entry.getKey()).query(entry.getValue().toString());
                    }
                    return matchBuilder;
                })).source(sourceBuilder -> sourceBuilder.filter(filterBuilder -> filterBuilder.includes(fieldsToInclude))));


        return elasticsearchClient.search(searchRequest, returnType);
    }

    @Override
    public <T>UpdateResponse<Object> update(String index, String id, T body) throws IOException {

        UpdateRequest<Object, Object> updateRequest = UpdateRequest.of(builder -> builder.index(index).id(id)
                .doc(body).docAsUpsert(Boolean.TRUE)
        );

        return elasticsearchClient.update(updateRequest, Map.class);
    }

    @Override
    public <T> SearchResponse<T> findByIds(String index, List<String> ids, Class<T> returnType) throws IOException {
        SearchRequest searchRequest = SearchRequest.of(builder -> builder.index(index).query(builder1 -> builder1.ids(builder2 -> builder2.values(ids
        ))));

        return elasticsearchClient.search(searchRequest, returnType);
    }

}
