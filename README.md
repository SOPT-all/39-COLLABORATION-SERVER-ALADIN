# Aladin Server

PLAY SOPT 39기 합동세미나 Mobile-Web 3조의 알라딘 클론 코딩 프로젝트 백엔드 레포지토리입니다.

## 프로젝트 소개

알라딘의 도서 조회와 중고 도서 거래 기능을 구현합니다.

세미나에서 학습한 내용을 실제 서비스 개발에 적용하고, 기능 구현을 넘어 설계와 기술적 선택의 근거를 이해하는 것을 목표로 합니다.

## 팀원

| 이름 | GitHub | 역할 |
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
| Database | Supabase PostgreSQL |
| ORM | Spring Data JPA |
| API Documentation | SpringDoc OpenAPI (Swagger UI) |
| Validation | Spring Validation |
| ETC | Lombok, Spring Boot DevTools |

## 현재 구현 내용

- 공통 API 응답 형식과 성공·에러 코드
- 전역 예외 및 Validation 예외 처리
- Swagger/OpenAPI 설정
- CORS 설정
- JPA 공통 생성·수정 시간 엔티티

도서 조회와 중고 도서 거래 API는 도메인별로 구현할 예정입니다.

## 프로젝트 구조

```text
src
├─ main
│  ├─ java/sopt/collaboration/aladin
│  │  ├─ AladinApplication.java
│  │  └─ global
│  │     ├─ code       # 성공·에러 코드
│  │     ├─ config     # Swagger, CORS 설정
│  │     ├─ dto        # 공통 응답 DTO
│  │     ├─ entity     # 공통 엔티티
│  │     └─ exception  # 전역 예외 처리
│  └─ resources
│     └─ application.yaml
└─ test
```

## 로컬 실행 방법

### 1. 저장소 클론

```bash
git clone https://github.com/SOPT-all/39-COLLABORATION-SERVER-ALADIN.git
cd 39-COLLABORATION-SERVER-ALADIN
```

### 2. 환경변수 설정

프로젝트 루트에 `.env` 파일을 만들고 Supabase PostgreSQL 접속 정보를 입력합니다.

```properties
DB_URL=jdbc:postgresql://<host>:<port>/<database>
DB_USERNAME=<username>
DB_PASSWORD=<password>
SERVER_PORT=8080
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

| 환경변수 | 필수 여부 | 설명 |
|---|---|---|
| `DB_URL` | 필수 | PostgreSQL JDBC URL |
| `DB_USERNAME` | 필수 | PostgreSQL 사용자 이름 |
| `DB_PASSWORD` | 필수 | PostgreSQL 비밀번호 |
| `SERVER_PORT` | 선택 | 애플리케이션 실행 포트, 기본값 `8080` |
| `CORS_ALLOWED_ORIGINS` | 선택 | 허용할 프론트엔드 Origin, 기본값 `http://localhost:3000` |

`.env` 파일은 Git으로 관리하지 않습니다.

### 3. 애플리케이션 실행

macOS / Linux:

```bash
./gradlew bootRun
```

Windows PowerShell:

```powershell
.\gradlew.bat bootRun
```

### 4. API 문서 확인

애플리케이션 실행 후 아래 주소에서 Swagger UI를 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui/index.html
```

`SERVER_PORT`를 변경했다면 URL의 포트도 같은 값으로 변경합니다.

## 협업 규칙

### Branch Convention

브랜치 이름은 `{type}/#{issue-number}-{description}` 형식을 사용합니다.

예시: `feat/#1-api-response-codes`

| 타입 | 용도 |
|---|---|
| `feat` | 기능 개발 |
| `fix` | 버그 수정 |
| `refactor` | 리팩터링 |
| `docs` | 문서 수정 |
| `chore` | 설정 및 유지보수 |

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

커밋 메시지는 `{type}: {description} - #{issue-number}` 형식을 사용합니다.

예시: `feat: 도서 상세 조회 API 구현 - #10`

## 개발 방향

- 기능 구현에 앞서 요구사항과 도메인을 충분히 이해합니다.
- 구현 방식과 기술적 선택의 근거를 설명할 수 있도록 합니다.
- 코드 리뷰를 통해 서로의 구현을 이해하고 개선합니다.
- 불필요한 복잡성을 줄이고 현재 요구사항에 적합한 설계를 지향합니다.

---

PLAY SOPT 39기 합동세미나 · Aladin Server
