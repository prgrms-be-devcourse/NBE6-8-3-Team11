# 🚀 팀원들을 위한 Docker 환경 마이그레이션 가이드

## 📋 TL;DR (요약)
**기존 개발 방식은 그대로 유지하면서, Docker는 선택적으로 사용 가능합니다.**

---

## 🎯 변경사항 요약

### ✅ **변경되지 않은 것들 (기존 방식 계속 사용 가능)**
- 로컬 개발 환경 (`npm run dev`, `./gradlew bootRun`)
- 코드 작성 방식
- Git 워크플로우
- IDE 설정

### 🔄 **새로 추가된 것들 (선택사항)**
- Docker 환경 (원하는 팀원만 사용)
- 통합 테스트 환경

---

## 👥 팀원별 대응 방안

### **Frontend 팀원들**

#### **변경 필요 없음 - 기존 방식 계속 사용**
```bash
# 기존 개발 방식 그대로
cd frontend
npm run dev
# → http://localhost:3000
```

#### **Docker 사용하고 싶다면 (선택)**
```bash
# 1. 환경 설정 (한 번만)
cp .env.example .env

# 2. 데이터베이스만 Docker로
./scripts/dev-start.sh

# 3. 앱은 기존 방식으로
cd frontend && npm run dev
```

### **Backend 팀원들**

#### **변경 필요 없음 - 기존 방식 계속 사용**
```bash
# 기존 개발 방식 그대로
cd backend
./gradlew bootRun --args='--spring.profiles.active=dev'
# → H2 DB 사용, localhost:8080
```

#### **MySQL 필요하다면 (선택)**
```bash
# MySQL만 Docker로 시작
docker-compose -f docker-compose.dev.yml up mysql redis -d

# 앱은 기존 방식으로 (dev → docker 프로파일로 변경)
./gradlew bootRun --args='--spring.profiles.active=docker'
```

---

## 🔧 API URL 불일치 해결

### **문제**: Frontend에서 backend 포트 불일치
```typescript
// 현재: localhost:3001 → 실제: localhost:8080
```

### **해결책**: 환경변수 활용
```bash
# frontend/.env.local 파일 생성 (각자)
NEXT_PUBLIC_API_URL=http://localhost:8080
```

---

## 📅 점진적 적용 계획

### **1주차: 선택적 사용**
- Docker 환경 구축 완료
- 원하는 팀원만 사용
- 기존 개발 방식 병행

### **2주차: 서서히 이주**
- 데이터베이스 통합 (MySQL)
- 환경 일치화
- 통합 테스트 환경 활용

### **3주차: 완전 통합**
- 모든 팀원 Docker 환경 사용
- CI/CD 연동
- 운영 환경과 동일한 개발 환경

---

## 🆘 문제 발생 시

### **"Docker 너무 복잡해요"**
→ 기존 방식 계속 사용하세요. 강제가 아닙니다.

### **"로컬 환경이 깨졌어요"**
→ Git으로 이전 상태 복구 가능합니다.

### **"포트가 안 맞아요"**
→ `.env.local` 파일로 API URL 수정하세요.

### **"빌드가 안 돼요"**
→ `npm run build` 로컬에서 먼저 테스트하세요.

---

## 💬 팀 커뮤니케이션

### **Slack 채널**
- `#docker-migration`: Docker 관련 질문/공유
- `#dev-support`: 개발 환경 문제 해결

### **주간 체크인**
- 월요일: Docker 사용 현황 공유
- 금요일: 문제점 및 개선사항 논의

---

## 🎁 Docker 사용의 장점 (동기부여)

### **개발자 개인**
- 환경 설정 시간 단축 (한 번 설정으로 끝)
- "내 컴퓨터에서는 돼요" 문제 해결
- 운영 환경과 동일한 환경에서 테스트

### **팀 전체**
- 환경 차이로 인한 버그 감소
- 새로운 팀원 온보딩 시간 단축
- 통합 테스트 환경 일치화

---

**💡 핵심**: 기존 개발 방식을 방해하지 않으면서, 선택적으로 Docker의 장점을 활용할 수 있도록 설계했습니다.