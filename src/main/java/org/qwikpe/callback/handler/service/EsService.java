package org.qwikpe.callback.handler.service;

import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface EsService {
     <TDocument> GetResponse<TDocument> findById(String index, String id, Class<TDocument> tDocumentClass, List<String> fieldsToInclude) throws IOException;

     <T> SearchResponse<T> findByFields(List<String> indexList, Map<String, Object> valueMap, Class<T> returnType, List<String> fieldsToInclude) throws IOException;

     <T> UpdateResponse<Object> update(String index, String id, T body) throws IOException;

     <T> SearchResponse<T> findByIds(String index, List<String> ids, Class<T> returnType) throws IOException;
}
