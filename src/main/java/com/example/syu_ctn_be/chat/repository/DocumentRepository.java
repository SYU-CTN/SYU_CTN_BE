package com.example.syu_ctn_be.chat.repository;

/**
 * RAG 의 Retrieval 단계.
 * 추후 벡터 DB(예: pgvector, Pinecone) 또는 ElasticSearch 구현체로 교체할 수 있도록
 * 인터페이스로 분리한다.
 */
public interface DocumentRepository {

    /**
     * 사용자 질의와 관련된 지식 조각들을 합쳐 하나의 컨텍스트 문자열로 반환한다.
     *
     * @param query 사용자 원문 질문
     * @return 시스템 프롬프트에 주입할 컨텍스트(없으면 빈 문자열)
     */
    String findRelevantContext(String query);
}
