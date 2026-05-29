package com.example.syu_ctn_be.rag;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@ConditionalOnProperty(name = "rag.provider", havingValue = "chroma")
public class ChromaClient {

    private final RestTemplate restTemplate;
    private final RagProperties ragProperties;
    private volatile String collectionId;

    public ChromaClient(RestTemplateBuilder restTemplateBuilder, RagProperties ragProperties) {
        this.restTemplate = restTemplateBuilder
                .rootUri(ragProperties.getChroma().getBaseUrl())
                .build();
        this.ragProperties = ragProperties;
    }

    public void upsert(List<String> ids, List<float[]> embeddings, List<Map<String, Object>> metadatas, List<String> documents) {
        String id = ensureCollection();
        restTemplate.postForEntity(
                collectionPath("/{collectionId}/upsert"),
                new UpsertRequest(ids, embeddings, metadatas, documents),
                Void.class,
                tenant(), database(), id);
    }

    public List<ChromaDocument> query(float[] queryEmbedding, int topK, Map<String, Object> where) {
        String id = ensureCollection();
        QueryResponse response = restTemplate.postForObject(
                collectionPath("/{collectionId}/query"),
                new QueryRequest(List.of(queryEmbedding), topK, emptyToNull(where), List.of("metadatas", "documents", "distances")),
                QueryResponse.class,
                tenant(), database(), id);

        if (response == null || response.ids() == null || response.ids().isEmpty()) {
            return List.of();
        }

        List<ChromaDocument> documents = new ArrayList<>();
        List<String> ids = valueAt(response.ids(), 0);
        List<String> texts = valueAt(response.documents(), 0);
        List<Map<String, Object>> metadatas = valueAt(response.metadatas(), 0);
        List<Double> distances = valueAt(response.distances(), 0);

        for (int i = 0; i < ids.size(); i++) {
            documents.add(new ChromaDocument(
                    ids.get(i),
                    i < texts.size() ? texts.get(i) : "",
                    i < metadatas.size() && metadatas.get(i) != null ? metadatas.get(i) : Map.of(),
                    i < distances.size() && distances.get(i) != null ? distances.get(i) : 1.0));
        }
        return documents;
    }

    public boolean existsById(String id) {
        GetResponse response = getByIds(List.of(id));
        return response != null && response.ids() != null && response.ids().contains(id);
    }

    public void deleteBySource(String source) {
        String id = ensureCollection();
        restTemplate.postForEntity(
                collectionPath("/{collectionId}/delete"),
                new DeleteRequest(null, Map.of("source", source)),
                Void.class,
                tenant(), database(), id);
    }

    private GetResponse getByIds(List<String> ids) {
        String collection = ensureCollection();
        return restTemplate.postForObject(
                collectionPath("/{collectionId}/get"),
                new GetRequest(ids, null, ids.size(), 0, List.of("metadatas", "documents")),
                GetResponse.class,
                tenant(), database(), collection);
    }

    private String ensureCollection() {
        if (collectionId != null) {
            return collectionId;
        }

        synchronized (this) {
            if (collectionId != null) {
                return collectionId;
            }

            String name = ragProperties.getChroma().getCollectionName();
            CollectionResponse collection = getCollection(name);
            if (collection == null) {
                collection = createCollection(name);
            }
            collectionId = collection.id();
            return collectionId;
        }
    }

    private CollectionResponse getCollection(String name) {
        try {
            CollectionResponse[] response = restTemplate.getForObject(
                    collectionsPath(),
                    CollectionResponse[].class,
                    tenant(),
                    database());
            if (response == null) {
                return null;
            }
            for (CollectionResponse collection : response) {
                if (name.equals(collection.name())) {
                    return collection;
                }
            }
            return null;
        } catch (RestClientException ex) {
            throw new IllegalStateException("Chroma collection 조회에 실패했습니다.", ex);
        }
    }

    private CollectionResponse createCollection(String name) {
        try {
            CollectionResponse collection = restTemplate.postForObject(
                    collectionsPath(),
                    new CreateCollectionRequest(name, Map.of("hnsw:space", "cosine"), true),
                    CollectionResponse.class,
                    tenant(),
                    database());
            if (collection == null) {
                throw new IllegalStateException("Chroma collection 생성 응답이 비어 있습니다.");
            }
            return collection;
        } catch (RestClientException ex) {
            CollectionResponse existing = getCollection(name);
            if (existing != null) {
                return existing;
            }
            throw new IllegalStateException("Chroma collection 생성에 실패했습니다.", ex);
        }
    }

    private String collectionsPath() {
        return "/api/v2/tenants/{tenant}/databases/{database}/collections";
    }

    private String collectionPath(String suffix) {
        return collectionsPath() + suffix;
    }

    private String tenant() {
        return ragProperties.getChroma().getTenant();
    }

    private String database() {
        return ragProperties.getChroma().getDatabase();
    }

    private Map<String, Object> emptyToNull(Map<String, Object> value) {
        return value == null || value.isEmpty() ? null : value;
    }

    private <T> List<T> valueAt(List<List<T>> values, int index) {
        if (values == null || values.size() <= index || values.get(index) == null) {
            return List.of();
        }
        return values.get(index);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CollectionResponse(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("metadata") Map<String, Object> metadata
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record CreateCollectionRequest(
            @JsonProperty("name") String name,
            @JsonProperty("metadata") Map<String, Object> metadata,
            @JsonProperty("get_or_create") Boolean getOrCreate
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record UpsertRequest(
            @JsonProperty("ids") List<String> ids,
            @JsonProperty("embeddings") List<float[]> embeddings,
            @JsonProperty("metadatas") List<Map<String, Object>> metadatas,
            @JsonProperty("documents") List<String> documents
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record QueryRequest(
            @JsonProperty("query_embeddings") List<float[]> queryEmbeddings,
            @JsonProperty("n_results") Integer nResults,
            @JsonProperty("where") Map<String, Object> where,
            @JsonProperty("include") List<String> include
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record QueryResponse(
            @JsonProperty("ids") List<List<String>> ids,
            @JsonProperty("documents") List<List<String>> documents,
            @JsonProperty("metadatas") List<List<Map<String, Object>>> metadatas,
            @JsonProperty("distances") List<List<Double>> distances
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record GetRequest(
            @JsonProperty("ids") List<String> ids,
            @JsonProperty("where") Map<String, Object> where,
            @JsonProperty("limit") Integer limit,
            @JsonProperty("offset") Integer offset,
            @JsonProperty("include") List<String> include
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record GetResponse(
            @JsonProperty("ids") List<String> ids,
            @JsonProperty("documents") List<String> documents,
            @JsonProperty("metadatas") List<Map<String, Object>> metadatas
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record DeleteRequest(
            @JsonProperty("ids") List<String> ids,
            @JsonProperty("where") Map<String, Object> where
    ) {
    }
}
