package com.example.syu_ctn_be.config;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    @Bean
    public Flyway flyway(DataSource dataSource) {
        System.out.println("🚀 [Flyway 강제 실행] 마이그레이션을 시작합니다...");

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .load();

        // 데이터베이스에 테이블 강제 생성 명령!
        flyway.migrate();

        System.out.println("✅ [Flyway 강제 실행] 테이블 생성이 완료되었습니다!");

        return flyway;
    }
}