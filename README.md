# Aladin Server

SOPT 합동세미나 알라딘 클론 코딩 프로젝트의 백엔드 레포지토리입니다.

## 프로젝트 소개

알라딘의 도서 조회 및 중고 도서 거래 기능을 구현하는 프로젝트입니다.

세미나에서 학습한 내용을 실제 서비스 개발에 적용하고, 단순한 기능 구현을 넘어 설계와 기술적 선택의 근거를 이해하는 것을 목표로 합니다.

## 팀원 소개 

| 이름 | 깃허브 | 역할 |
|---|---|---|
| 장현준 | [guswnsj0112](https://github.com/guswnsj0112) | Server Lead |
| 채윤지 | [KateteDeveloper](https://github.com/KateteDeveloper) | Server Developer |
| 이수민 | [isumin418](https://github.com/isumin418) | Server Developer |

## 기술 스택

| 구분 | 기술 |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.2 |
| Build | Gradle |
| Database | MySQL |
| ORM | Spring Data JPA |
| API Documentation | SpringDoc OpenAPI (Swagger UI) |
| Validation | Spring Validation |
| ETC | Lombok, Spring Boot DevTools |

## 주요 기능
> 숨김 처리

## 프로젝트 구조

Spring Boot 기반의 REST API 서버로 구성하며, 도메인별 책임을 분리하여 개발할 예정입니다.

프로젝트 구조와 공통 규칙은 팀원 간 논의를 통해 확정하고, 개발 과정에서 필요한 부분을 점진적으로 개선합니다.

## 로컬 실행 방법

### 1. 저장소 클론

GitHub에서 저장소를 클론합니다.

### 2. 환경변수 설정

프로젝트 루트의 `.env.example` 파일을 복사하여 `.env` 파일을 생성합니다.

각자의 로컬 환경에 맞게 데이터베이스 접속 정보와 API 키를 설정합니다.

| 환경변수 | 설명 |
|---|---|
| `SERVER_PORT` | 애플리케이션 실행 포트 |
| `DB_URL` | MySQL JDBC URL |
| `DB_USERNAME` | MySQL 사용자 이름 |
| `DB_PASSWORD` | MySQL 비밀번호 |
| `OPENAI_API_KEY` | AI 리뷰 요약 기능에 사용할 API 키 |

`.env` 파일은 Git으로 관리하지 않습니다.

### 3. 데이터베이스 준비

MySQL에 `aladin` 데이터베이스를 생성하고 환경변수에 접속 정보를 설정합니다.

### 4. 애플리케이션 실행

macOS / Linux: `./gradlew bootRun`

Windows: `gradlew.bat bootRun`

### 5. API 문서 확인

애플리케이션 실행 후 Swagger UI에서 API 명세를 확인할 수 있습니다.

`http://localhost:8080/swagger-ui/index.html`

## 협업 규칙

### Branch Convention

| 브랜치 | 용도 |
|---|---|
| `main` | 안정적인 코드 관리 |
| `develop` | 기능 통합 |
| `feat/*` | 기능 개발 |
| `fix/*` | 버그 수정 |
| `refactor/*` | 리팩터링 |

### Commit Convention

| 타입 | 설명 |
|---|---|
| `feat` | 새로운 기능 추가 |
| `fix` | 버그 수정 |
| `refactor` | 코드 리팩터링 |
| `docs` | 문서 수정 |
| `style` | 코드 스타일 변경 |
| `test` | 테스트 코드 |
| `build` | 빌드 및 의존성 변경 |
| `chore` | 기타 설정 및 유지보수 |
| `ci` | CI/CD 설정 |

커밋 메시지는 변경 사항을 명확하게 이해할 수 있도록 작성합니다.

예시: `feat(book): 도서 상세 조회 API 구현`

## 개발 방향

- 기능 구현에 앞서 요구사항과 도메인을 충분히 이해합니다.
- 구현 방식에 대해 질문하고 선택의 근거를 설명할 수 있도록 합니다.
- 팀원 간 코드 리뷰를 통해 서로의 구현을 이해하고 개선합니다.
- 불필요한 복잡성을 줄이고 현재 요구사항에 적합한 설계를 지향합니다.

---

SOPT 합동세미나 · Aladin Server
