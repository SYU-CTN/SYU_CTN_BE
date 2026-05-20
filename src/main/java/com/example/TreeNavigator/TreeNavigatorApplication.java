package com.example.TreeNavigator;

import org.flywaydb.core.Flyway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TreeNavigatorApplication {

	public static void main(String[] args) {
		try {
			System.out.println("====== [Flyway] 강제 마이그레이션 기동 시작 ======");
			Flyway flyway = Flyway.configure()
					.dataSource("jdbc:mysql://127.0.0.1:3306/tree_navigator?serverTimezone=Asia/Seoul&characterEncoding=UTF-8", "root", "1111")
					.baselineOnMigrate(true)
					.load();
			flyway.migrate();
			System.out.println("====== [Flyway] 강제 마이그레이션 성공 완수 ======");
		} catch (Exception e) {
			System.out.println("====== [Flyway] 에러 발생: " + e.getMessage());
		}

		SpringApplication.run(TreeNavigatorApplication.class, args);
	}
}