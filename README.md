# 🐾 PetMatching - 반려동물 입양 매칭 플랫폼

> 유기동물과 입양 희망자를 연결하는 종합적인 반려동물 입양 매칭 서비스

[![배포 상태](https://img.shields.io/badge/배포-Live-brightgreen)](https://nbe-6-8-2-team11.vercel.app)
[![백엔드 API](https://img.shields.io/badge/API-Running-blue)](https://nbe6-8-3-team11.fly.dev)
[![라이센스](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

## 📖 프로젝트 개요

**PetMatching**은 유기동물의 새로운 가족을 찾아주는 종합적인 반려동물 입양 매칭 플랫폼입니다. 보호소, 입양 희망자, 그리고 유기동물을 하나로 연결하여 더 많은 동물들이 따뜻한 가정을 찾을 수 있도록 돕습니다.

### 🎯 프로젝트 목표
- **접근성 향상**: 누구나 쉽게 입양 정보에 접근할 수 있는 플랫폼 제공
- **투명성 확보**: 입양 과정의 모든 단계를 투명하게 공개
- **효율적 매칭**: AI 기반 매칭 시스템으로 최적의 입양 연결
- **지속적 관리**: 입양 후에도 지속적인 케어와 상담 서비스 제공

## ✨ 주요 기능

### 🏠 메인 서비스
- **🔍 반려동물 탐색**: 필터링 및 검색 기능으로 원하는 반려동물 찾기
- **📝 입양 신청**: 간편한 온라인 입양 신청 및 서류 관리
- **💬 실시간 채팅**: WebSocket 기반 실시간 상담 및 문의
- **📊 매칭 시스템**: 사용자 선호도와 반려동물 특성을 기반으로 한 맞춤 추천

### 👥 사용자 기능
- **🔐 소셜 로그인**: 카카오 OAuth2를 통한 간편 회원가입/로그인
- **👤 프로필 관리**: 입양 이력, 관심 동물, 선호도 설정
- **📱 반응형 UI**: 모바일, 태블릿, 데스크톱 모든 환경에서 최적화
- **🔔 알림 시스템**: 입양 진행 상황 및 중요 공지사항 알림

### 🏢 관리자 기능
- **📋 보호소 관리**: 보호소 정보 등록 및 관리
- **🐕 동물 등록**: 보호동물 정보 등록 및 상태 관리
- **📈 통계 대시보드**: 입양률, 방문자 통계 등 데이터 분석
- **🔧 시스템 관리**: 사용자 관리, 신고 처리, 시스템 설정

## 🛠 기술 스택

### Frontend (프론트엔드)
- **Framework**: Next.js 15.4.3 (React 19)
- **Language**: TypeScript 5.x
- **Styling**: Tailwind CSS 3.4.15
- **State Management**: Zustand 5.0.7
- **HTTP Client**: Axios 1.11.0
- **Real-time**: STOMP.js, SockJS
- **Date Handling**: date-fns 4.1.0

### Backend (백엔드)
- **Framework**: Spring Boot 3.5.3
- **Language**: Java 21 + Kotlin 1.9.25 (하이브리드)
- **Database**: PostgreSQL 42.7.2 (Production), H2 (Development)
- **Cache**: Redis (실시간 채팅, 세션 관리)
- **Authentication**: Spring Security + JWT + OAuth2 (Kakao)
- **Real-time**: WebSocket + STOMP
- **Documentation**: SpringDoc OpenAPI 2.8.0
- **Monitoring**: Spring Actuator

### Infrastructure (인프라)
- **Frontend Hosting**: Vercel (자동 배포)
- **Backend Hosting**: Fly.io (PostgreSQL + Redis 포함)
- **CI/CD**: GitHub Actions
- **Monitoring**: Fly.io 대시보드, Spring Actuator

## 🏗 프로젝트 구조

### 백엔드 아키텍처 (Domain-Driven Design)
```
backend/src/main/java/com/back/
├── 📁 domain/                     # 도메인별 패키지
│   ├── 👤 member/                 # 회원 도메인
│   │   ├── entity/               # 엔티티
│   │   ├── dto/                  # 요청/응답 DTO
│   │   ├── repository/           # 데이터 접근 계층
│   │   ├── service/              # 비즈니스 로직
│   │   ├── controller/           # API 컨트롤러
│   │   └── enums/                # 열거형
│   ├── 🐕 pet/                    # 반려동물 도메인
│   ├── 📋 adoption/               # 입양 신청 도메인
│   ├── 💬 chat/                   # 채팅 도메인
│   ├── 🏥 care/                   # 돌봄 서비스 도메인
│   ├── 🏢 shelter/                # 보호소 도메인
│   ├── 📝 applicant/              # 신청자 도메인
│   └── 🔔 notification/           # 알림 도메인
└── 🌐 global/                     # 전역 설정
    ├── config/                   # 설정 클래스 (Security, WebSocket 등)
    ├── security/                 # JWT, OAuth2 인증
    ├── exception/                # 전역 예외 처리
    ├── common/                   # 공통 기능 (응답 형식, 기본 엔티티)
    └── util/                     # 유틸리티
```

### 프론트엔드 구조 (Feature-Based Architecture)
```
frontend/src/
├── 📱 app/                        # Next.js App Router
│   ├── page.tsx                  # 홈페이지
│   ├── layout.tsx                # 전역 레이아웃
│   ├── 🔍 pets/                   # 반려동물 탐색
│   ├── 📝 apply/                  # 입양 신청
│   ├── 👤 profile/                # 사용자 프로필
│   ├── 💬 allchat/                # 채팅 (전체)
│   ├── 🔐 login/                  # 로그인
│   ├── ✍️ signup/                 # 회원가입
│   ├── 🏢 admin/                  # 관리자 페이지
│   └── 📞 contact/                # 문의하기
├── 🎨 features/                   # 기능별 컴포넌트
│   ├── home/                     # 홈페이지 관련
│   ├── gallery/                  # 갤러리 관련
│   └── [feature]/                # 기타 기능별 디렉토리
├── 🧩 shared/                     # 공통 모듈
│   ├── components/               # 재사용 컴포넌트
│   ├── services/                 # API 서비스
│   ├── types/                    # TypeScript 타입 정의
│   └── utils/                    # 유틸리티 함수
└── 🎯 context/                    # React Context (전역 상태)
```

## 🚀 빠른 시작 가이드

### 📋 사전 요구사항
- **Java**: 21 (OpenJDK 권장)
- **Node.js**: 20 LTS 이상
- **Database**: PostgreSQL 13+ (운영), H2 (개발)
- **IDE**: IntelliJ IDEA (권장)
- **Git**: 최신 버전

### 🔧 로컬 개발 환경 설정

#### 1. 저장소 클론 및 기본 설정
```bash
# 저장소 클론
git clone https://github.com/prgrms-be-devcourse/NBE6-8-2-Team11.git
cd NBE6-8-2-Team11

# 개발 브랜치로 전환
git checkout develop
```

#### 2. 백엔드 설정 및 실행
```bash
cd backend

# Java 버전 확인 (Java 21 필요)
java --version

# 애플리케이션 빌드
./gradlew build

# H2 데이터베이스로 개발 모드 실행
./gradlew bootRun --args='--spring.profiles.active=dev'

# API 문서 확인: http://localhost:8080/swagger-ui.html
# H2 콘솔: http://localhost:8080/h2-console
```

#### 3. 프론트엔드 설정 및 실행
```bash
cd frontend

# 의존성 설치
npm install

# 개발 서버 시작
npm run dev

# 브라우저에서 http://localhost:3000 접속
```

#### 4. 전체 서비스 Docker로 실행 (선택사항)
```bash
# 루트 디렉토리에서
docker-compose up -d

# 서비스 확인
docker-compose logs -f
```

### 🔑 환경 변수 설정

#### 백엔드 환경 변수 (.env)
```env
# 데이터베이스 설정
MYSQL_HOST=localhost
MYSQL_PORT=3306  
MYSQL_DATABASE=petmatching
MYSQL_USERNAME=your_username
MYSQL_PASSWORD=your_password

# JWT 설정
JWT_SECRET=your-super-secret-key-minimum-256-bits
JWT_ACCESS_TOKEN_EXPIRATION=PT15M
JWT_REFRESH_TOKEN_EXPIRATION=PT168H

# OAuth2 설정 (카카오)
KAKAO_CLIENT_ID=your_kakao_client_id
KAKAO_CLIENT_SECRET=your_kakao_client_secret
OAUTH2_REDIRECT_URI=http://localhost:8080/login/oauth2/code/kakao

# Redis 설정
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
```

#### 프론트엔드 환경 변수 (.env.local)
```env
# 백엔드 API URL
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080

# 기타 설정
NEXT_TELEMETRY_DISABLED=1
```

## 🌐 배포 및 운영

### 📡 배포 환경
- **🌍 프론트엔드**: https://nbe-6-8-2-team11.vercel.app
- **⚡ 백엔드**: https://nbe6-8-3-team11.fly.dev
- **📊 API 문서**: https://nbe6-8-3-team11.fly.dev/swagger-ui.html
- **💓 헬스체크**: https://nbe6-8-3-team11.fly.dev/actuator/health

### 🔄 자동 배포 프로세스
```
코드 푸시 → GitHub Actions → 테스트 실행 → Slack 알림 → 자동 배포
```

1. **개발**: `develop` 브랜치에 푸시
2. **테스트**: GitHub Actions CI 파이프라인 실행
3. **리뷰**: PR 생성 및 팀원 코드 리뷰
4. **배포**: `main` 브랜치 머지 시 자동 배포
5. **모니터링**: Slack 알림 및 헬스체크 확인

## 👥 개발 팀 가이드

### 🌿 Git Flow 전략

#### 브랜치 구조
```
main (배포 및 완성)
└── develop (개발 메인)
    ├── feature/사용자-인증-시스템
    ├── feature/반려동물-등록-API  
    ├── feature/채팅-시스템-구현
    └── hotfix/로그인-버그-수정
```

#### 워크플로우
```bash
# 1. 새 기능 개발 시작
git checkout develop
git pull origin develop
git checkout -b feature/기능명-이슈번호

# 2. 개발 및 커밋
git add .
git commit -m "feat(domain): 기능 설명"

# 3. PR 생성 및 리뷰
git push origin feature/기능명-이슈번호
# GitHub에서 PR 생성

# 4. 리뷰 완료 후 develop에 머지
# 5. develop이 안정화되면 main으로 머지하여 배포
```

### 📝 커밋 컨벤션
```
<type>(<scope>): <subject>

[optional body]
[optional footer]
```

**타입 종류**:
- `feat`: 새로운 기능 추가
- `fix`: 버그 수정  
- `docs`: 문서 수정
- `style`: 코드 포맷팅
- `refactor`: 코드 리팩토링
- `test`: 테스트 코드 추가/수정
- `chore`: 빌드 업무, 패키지 매니저 설정

**예시**:
```bash
feat(auth): JWT 기반 인증 시스템 구현

- Spring Security와 JWT를 활용한 인증 구현
- Access Token, Refresh Token 발급 기능
- 카카오 OAuth2 소셜 로그인 연동

Resolves: #123
```

### 🔄 PR(Pull Request) 규칙

#### PR 제목 형식
```
[<TYPE>] <간단한 설명> (#이슈번호)

예: [FEAT] 사용자 인증 시스템 구현 (#12)
```

#### PR 체크리스트
- [ ] 최신 develop 브랜치와 동기화
- [ ] 로컬에서 테스트 완료
- [ ] 커밋 메시지 컨벤션 준수
- [ ] 코드 리뷰를 위한 명확한 설명 작성
- [ ] 관련 이슈 번호 명시

## 🧪 테스트

### 백엔드 테스트 실행
```bash
cd backend

# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests UserServiceTest

# 테스트 리포트 확인: build/reports/tests/test/index.html
```

### 프론트엔드 테스트 실행
```bash
cd frontend

# 테스트 실행 (구현 예정)
npm test

# 린트 검사
npm run lint

# 빌드 테스트
npm run build
```

## 📊 API 문서

### 🔗 Swagger UI
- **로컬**: http://localhost:8080/swagger-ui.html
- **운영**: https://nbe6-8-3-team11.fly.dev/swagger-ui.html

### 🔍 주요 API 엔드포인트

#### 인증 관련
```http
POST /api/auth/login          # 로그인
POST /api/auth/refresh        # 토큰 갱신
GET  /api/auth/me            # 내 정보 조회
```

#### 반려동물 관련  
```http
GET    /api/pets             # 반려동물 목록 조회
GET    /api/pets/{id}        # 반려동물 상세 조회
POST   /api/pets             # 반려동물 등록 (관리자)
PUT    /api/pets/{id}        # 반려동물 정보 수정
DELETE /api/pets/{id}        # 반려동물 삭제
```

#### 입양 신청 관련
```http
POST   /api/adoptions        # 입양 신청
GET    /api/adoptions/my     # 내 입양 신청 조회
PUT    /api/adoptions/{id}   # 입양 신청 상태 변경
```

#### 채팅 (WebSocket)
```
CONNECT /ws-chat              # WebSocket 연결
SEND    /app/chat.sendMessage # 메시지 전송  
SUBSCRIBE /topic/chat/{roomId} # 채팅방 구독
```

## 🤝 기여하기

### 📋 코딩 컨벤션

#### 백엔드 (Java/Kotlin)
- **네이밍**: camelCase (메서드, 변수), PascalCase (클래스)
- **패키지**: 도메인별 패키지 구조 준수
- **주석**: JavaDoc 형식으로 API 문서화
- **예외**: 커스텀 예외 클래스 활용

#### 프론트엔드 (TypeScript/React)
- **네이밍**: camelCase (변수, 함수), PascalCase (컴포넌트)
- **컴포넌트**: 함수형 컴포넌트 + Hooks 사용
- **스타일링**: Tailwind CSS 유틸리티 클래스
- **타입**: 엄격한 TypeScript 타입 정의

## 🚨 문제 해결 방식

### 자주 발생하는 문제들

#### 🔧 백엔드 관련
```bash
# 포트 충돌 문제
netstat -an | grep :8080
kill -9 $(lsof -ti:8080)

# 캐시 정리
./gradlew clean build

# 데이터베이스 연결 문제  
# application.yml의 데이터베이스 설정 확인
```

#### 🎨 프론트엔드 관련
```bash
# 의존성 문제
rm -rf node_modules package-lock.json
npm install

# 빌드 오류
npm run lint --fix
npm run build

# 캐시 정리
npm run dev -- --reset-cache
```

#### 🐳 Docker 관련
```bash
# 컨테이너 재시작
docker-compose down
docker-compose up -d

# 로그 확인
docker-compose logs -f backend
docker-compose logs -f frontend
```

## 📞 기타
## 📄 라이센스

이 프로젝트는 [MIT License](LICENSE)에 따라 라이센스가 부여됩니다.

## 🎉 멘션

이 프로젝트는 **프로그래머스 데브코스 백엔드 6기**의 팀 프로젝트로 진행되었습니다. 유기동물들이 따뜻한 가정을 찾을 수 있도록 도움을 주는 의미 있는 서비스를 만들기 위해 노력했습니다.

더 많은 반려동물들이 행복한 가정을 찾을 수 있도록, 앞으로도 지속적으로 서비스를 개선해 나가겠습니다. 🐾❤️

---

**마지막 업데이트**: 2025년 8월 28일  
**현재 버전**: v1.0.0  
**팀**: NBE6-8-3-Team11

*"모든 동물은 사랑받을 권리가 있습니다" 🐕🐱*