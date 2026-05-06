package com.example.syu_ctn_be.chat.repository;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

/**
 * 가상 구현체. 실제 벡터 검색 인프라가 준비되기 전까지 키워드 기반으로 동작한다.
 * 단순 키워드 포함 여부 검색이지만 RAG 의 흐름(검색 → 시스템 프롬프트 주입)을
 * 그대로 시연할 수 있도록 설계되었다.
 */
@Repository
public class InMemoryDocumentRepository implements DocumentRepository {

    /**
     * 데모용 지식 베이스. 운영에서는 외부 저장소에서 로드해야 한다.
     * key = 매칭 키워드, value = 컨텍스트로 주입할 본문.
     */
    private static final Map<String, String> KNOWLEDGE_BASE = buildKnowledgeBase();

    private static Map<String, String> buildKnowledgeBase() {
        Map<String, String> kb = new LinkedHashMap<>();
        kb.put("커리큘럼",
                "삼육대학교 컴퓨터학부의 표준 커리큘럼은 1학년 기초 SW, 2학년 자료구조/알고리즘, "
                        + "3학년 시스템/데이터베이스, 4학년 캡스톤 프로젝트로 구성된다.");
        kb.put("졸업요건",
                "졸업을 위해서는 전공필수 과목을 모두 이수하고 총 130학점 이상을 취득해야 한다. "
                        + "전공심화 트랙은 추가 학점 요건이 있을 수 있다.");
        kb.put("수강신청",
                "수강신청은 매 학기 시작 전 정해진 기간에 수강신청 시스템에서 진행되며, "
                        + "선이수 과목 위반 시 자동 거절된다.");
        return kb;
    }

    @Override
    public String findRelevantContext(String query) {
        if (query == null || query.isBlank()) {
            return "";
        }
        String normalized = query.toLowerCase();

        // 키워드가 질의에 포함된 항목들을 모아 컨텍스트로 합친다.
        return KNOWLEDGE_BASE.entrySet().stream()
                .filter(entry -> normalized.contains(entry.getKey().toLowerCase()))
                .map(entry -> "- " + entry.getValue())
                .collect(Collectors.joining("\n"));
    }
}
