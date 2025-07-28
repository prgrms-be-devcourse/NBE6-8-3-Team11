# 포트 설정 원복 가이드

만약 기존 포트 설정으로 돌리고 싶다면:

## Option 1: Frontend API URL만 환경변수로 처리
```typescript
// frontend/src/shared/services/apiClient.ts
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:3001/api';
```

각 팀원이 개별적으로:
```bash
echo "NEXT_PUBLIC_API_URL=http://localhost:8080" > frontend/.env.local
```

## Option 2: Backend 포트를 3001로 변경
```yaml
# backend/src/main/resources/application.yml
server:
  port: 3001
```

## Option 3: 환경별 다른 포트 사용
- 로컬 개발: Frontend 3000, Backend 8080
- Docker 환경: Frontend 3000, Backend 8080
- 환경변수로 API URL 관리

## 권장사항
현재 설정(8080 통일)을 유지하는 것을 권장합니다:
- 표준 포트 사용
- Docker 환경과 일관성
- 팀원들 혼동 최소화