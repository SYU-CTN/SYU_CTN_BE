package com.example.syu_ctn_be.rag;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rag")
public class RagProperties {

    private String provider = "memory";
    private String knowledgeLocation = "classpath:knowledge/*.txt";
    private int topK = 5;
    private double similarityThreshold = 0.65;
    private int maxChunkLength = 900;
    private int chunkOverlap = 120;
    private Chroma chroma = new Chroma();

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getKnowledgeLocation() {
        return knowledgeLocation;
    }

    public void setKnowledgeLocation(String knowledgeLocation) {
        this.knowledgeLocation = knowledgeLocation;
    }

    public int getTopK() {
        return topK;
    }

    public void setTopK(int topK) {
        this.topK = topK;
    }

    public double getSimilarityThreshold() {
        return similarityThreshold;
    }

    public void setSimilarityThreshold(double similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
    }

    public int getMaxChunkLength() {
        return maxChunkLength;
    }

    public void setMaxChunkLength(int maxChunkLength) {
        this.maxChunkLength = maxChunkLength;
    }

    public int getChunkOverlap() {
        return chunkOverlap;
    }

    public void setChunkOverlap(int chunkOverlap) {
        this.chunkOverlap = chunkOverlap;
    }

    public Chroma getChroma() {
        return chroma;
    }

    public void setChroma(Chroma chroma) {
        this.chroma = chroma;
    }

    public static class Chroma {
        private String baseUrl = "http://localhost:8000";
        private String tenant = "default_tenant";
        private String database = "default_database";
        private String collectionName = "curriculum_chatbot_collection";

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getCollectionName() {
            return collectionName;
        }

        public void setCollectionName(String collectionName) {
            this.collectionName = collectionName;
        }

        public String getTenant() {
            return tenant;
        }

        public void setTenant(String tenant) {
            this.tenant = tenant;
        }

        public String getDatabase() {
            return database;
        }

        public void setDatabase(String database) {
            this.database = database;
        }
    }
}
