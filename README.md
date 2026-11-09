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
JPA_DDL_AUTO=validate
SERVER_PORT=8080
CORS_ALLOWED_ORIGINS=http://localhost:5173
```

| 환경변수 | 필수 여부 | 설명 |
|---|---|---|
| `DB_URL` | 필수 | PostgreSQL JDBC URL |
| `DB_USERNAME` | 필수 | PostgreSQL 사용자 이름 |
| `DB_PASSWORD` | 필수 | PostgreSQL 비밀번호 |
| `JPA_DDL_AUTO` | 선택 | Hibernate 스키마 처리 방식, 기본값 `validate` |
| `SERVER_PORT` | 선택 | 애플리케이션 실행 포트, 기본값 `8080` |
| `CORS_ALLOWED_ORIGINS` | 선택 | 허용할 프론트엔드 Origin, 기본값 `http://localhost:5173` |

`.env` 파일은 Git으로 관리하지 않습니다.

공유·운영 DB에서는 기본값인 `validate`를 사용합니다. 격리된 로컬 개발 DB에서 Hibernate가 스키마를 자동 변경해야 할 때만 `JPA_DDL_AUTO=update`를 설정합니다.

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

## 자동 배포 최초 설정

`main` 브랜치에 변경사항이 반영되면 GitHub Actions가 JAR를 빌드해 EC2로 전송하고 `aladin.service`를 재시작합니다. 배포 후 Swagger UI가 1분 안에 응답하지 않으면 이전 JAR로 복구합니다.

### 빌드와 운영 DB 설정 분리

GitHub Actions의 `Build and Test` 단계는 외부 DB에 연결하지 않고 단위 테스트와 JAR 생성을 수행합니다. DB 접속 정보는 빌드된 JAR에 포함되지 않습니다.

배포된 애플리케이션은 EC2 런타임의 `.env` 또는 `aladin.service`의 systemd 환경 설정에서 `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`를 주입받아 운영 DB에 연결합니다. 프로젝트 루트의 `.env`를 사용한다면 `aladin.service`의 `WorkingDirectory`가 프로젝트 루트를 가리켜야 합니다.

최초 배포 전에는 값을 출력하지 않고 설정 경로와 필수 항목의 존재 여부만 확인합니다.

```bash
sudo systemctl show aladin \
  --property=WorkingDirectory \
  --property=EnvironmentFiles

cd ~/39-COLLABORATION-SERVER-ALADIN
grep -q '^DB_URL=' .env && echo "DB_URL configured"
grep -q '^DB_USERNAME=' .env && echo "DB_USERNAME configured"
grep -q '^DB_PASSWORD=' .env && echo "DB_PASSWORD configured"
```

DB 접속 정보의 실제 값은 GitHub Actions 로그, 코드, 문서에 출력하거나 커밋하지 않습니다.

### 1. 배포 전용 SSH 키 생성

로컬 Windows PowerShell에서 실행합니다.

```powershell
ssh-keygen -t ed25519 -f "$env:USERPROFILE\.ssh\aladin-deploy" -C "github-actions-aladin"
```

생성된 공개키를 기존 EC2 접속 키로 서버에 등록합니다.

```powershell
Get-Content "$env:USERPROFILE\.ssh\aladin-deploy.pub" | ssh -i "<기존_EC2_PEM_경로>" ubuntu@<EC2_PUBLIC_DNS> "umask 077; mkdir -p ~/.ssh; cat >> ~/.ssh/authorized_keys"
```

개인키 `aladin-deploy`는 EC2나 저장소에 올리지 않습니다.

### 2. GitHub production Environment Secrets 등록

GitHub 저장소의 `Settings` → `Environments` → `production` → `Environment secrets`에 다음 값을 등록합니다.

| Secret | 값 |
|---|---|
| `EC2_HOST` | EC2 Public DNS 또는 Elastic IP |
| `EC2_USER` | `ubuntu` |
| `EC2_SSH_PRIVATE_KEY` | `aladin-deploy` 개인키 전체 내용 |
| `EC2_KNOWN_HOSTS` | 신뢰할 수 있는 환경에서 확인한 EC2 SSH 호스트 키 |

호스트 키는 EC2에 정상 접속한 로컬 환경에서 다음 명령으로 확인할 수 있습니다.

```powershell
ssh-keyscan -H <EC2_PUBLIC_DNS>
```

출력된 호스트 키의 지문을 기존 SSH 접속에서 확인한 지문과 대조한 뒤 전체 줄을 `EC2_KNOWN_HOSTS`에 저장합니다.

### 3. EC2 systemd 재시작 권한 설정

EC2에서 다음 파일을 엽니다.

```bash
sudo visudo -f /etc/sudoers.d/aladin-deploy
```

아래 한 줄을 추가합니다.

```text
ubuntu ALL=(root) NOPASSWD: /usr/bin/systemctl restart aladin
```

저장 후 문법을 검사합니다.

```bash
sudo visudo -cf /etc/sudoers.d/aladin-deploy
```

### 4. EC2 보안 그룹 확인

GitHub-hosted runner가 EC2의 SSH 포트에 접근할 수 있어야 합니다. SSH 포트를 `0.0.0.0/0`에 상시 공개하지 말고, 팀의 보안 정책에 맞는 제한된 접근 방식이나 AWS Systems Manager를 사용합니다.

설정이 끝나면 GitHub의 `Actions` → `Aladin CI/CD` → `Run workflow`에서 최초 배포를 검증합니다. 이후에는 `main` 브랜치에 변경사항이 반영될 때 자동으로 배포됩니다.

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
