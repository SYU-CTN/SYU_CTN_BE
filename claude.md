# Curriculum 커리큘럼 페이지 개발 가이드

## 코드 스타일
- 들여쓰기: Java는 4 스페이스, React/JS는 2 스페이스 사용 (탭 사용 금지)
- 명명 규칙: 
  - 클래스/컴포넌트: PascalCase
  - 변수/함수/메서드: camelCase
  - 상수/DB컬럼: SCREAMING_SNAKE_CASE
- 함수명은 동사로 시작: `getCurriculum`, `saveSubject`
- Backend (Spring Boot/JPA):
  - Setter 사용을 지양하고 의미 있는 비즈니스 메서드 사용
- Frontend (React):
  - 함수형 컴포넌트 및 Hook 사용 필수
  - API 호출은 `Axios` 인스턴스 활용

## 협업 및 접근 권한 규칙

- 폴더 및 파일 접근 제한:
  - 본인의 담당 파트(예: `frontend/` 또는 `backend/`) 외의 폴더 및 파일은 임의로 수정하거나 삭제하지 않습니다.
  - 공통 설정 파일(`build.gradle`, `package.json`, `.env` 등)을 수정해야 할 경우, 사전에 팀원들과 반드시 논의합니다.
- 브랜치(Branch) 권한 및 보호:
  - `main` 및 `develop` 브랜치에 대한 직접 푸시(Direct Push)는 엄격히 금지됩니다.
- Pull Request (PR) 및 코드 병합:
  - 작업 완료 후 `develop` 브랜치로 PR을 생성합니다.