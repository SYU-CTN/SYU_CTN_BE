package com.example.syu_ctn_be.rag;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class VectorDbInitializer implements CommandLineRunner {

    private final ChromaKnowledgeIngestionService chromaKnowledgeIngestionService;

    @Override
    public void run(String... args) throws Exception {
        try {
            log.info("🚀 벡터 DB 지식 주입을 시작합니다...");
            chromaKnowledgeIngestionService.ingest();
            log.info("✅ 벡터 DB 지식 주입 성공!");
        } catch (Exception e) {
            // 👈 여기서 모든 에러를 잡아 삼켜버려야 스프링 부트 서버가 꺼지지 않습니다.
            log.warn("⚠️ [경고] ChromaDB가 꺼져 있거나 연결에 실패하여 지식 주입을 건너뜁니다.");
            log.info("ℹ️ 현재 일반 DB(MySQL) 기능은 정상 작동하므로 그대로 테스트를 진행하셔도 됩니다.");
        }
    }
}