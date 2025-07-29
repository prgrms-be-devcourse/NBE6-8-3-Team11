# 🐳 PetMatching Docker 개발 환경 가이드

## 🚀 빠른 시작

### 1. 환경 설정
```bash
# 환경 변수 설정
cp .env.example .env
# .env 파일을 열어서 필요한 값들 수정

# 스크립트 실행 권한 부여
chmod +x scripts/*.sh
```

### 2. 전체 환경 실행 (프로덕션 모드)
```bash
# 한 번에 모든 서비스 시작
./scripts/start-local.sh

# 접속 확인
# Frontend: http://localhost:3000
# Backend: http://localhost:8080
# MySQL: localhost:3306
# Redis: localhost:6379
```

### 3. 개발 환경 실행 (DB만, 앱은 로컬)
```bash
# 데이터베이스만 시작
./scripts/dev-start.sh

# 별도 터미널에서 백엔드 실행
cd backend
./gradlew bootRun --args='--spring.profiles.active=dev'

# 별도 터미널에서 프론트엔드 실행  
cd frontend
npm run dev
```

## 📋 사용 가능한 명령어

```bash
# 🚀 시작
./scripts/start-local.sh    # 전체 환경 시작
./scripts/dev-start.sh      # 개발 환경 (DB만)

# 🛑 중지
./scripts/stop.sh           # 모든 환경 중지

# 📝 로그 확인
./scripts/logs.sh           # 전체 로그
./scripts/logs.sh backend   # 백엔드 로그
./scripts/logs.sh frontend  # 프론트엔드 로그
./scripts/logs.sh mysql     # MySQL 로그

# 🔧 기타 유용한 명령어
docker-compose ps           # 컨테이너 상태 확인
docker-compose restart      # 서비스 재시작
docker-compose pull         # 이미지 업데이트
```

## 🏗️ 아키텍처

```
┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │
│   (Next.js)     │────│  (Spring Boot)  │
│   Port: 3000    │    │   Port: 8080    │
└─────────────────┘    └─────────────────┘
                              │
                    ┌─────────┴─────────┐
                    │                   │
            ┌───────▼─────┐    ┌────────▼──────┐
            │   MySQL     │    │    Redis      │
            │ Port: 3306  │    │  Port: 6379   │
            └─────────────┘    └───────────────┘
```

## 🔧 개발 팁

### 환경별 설정
- **Development**: H2 DB (빠른 개발)
- **Docker**: MySQL + Redis (운영 환경과 동일)
- **Production**: MySQL + Redis + 보안 강화

### 데이터베이스 접속
```bash
# MySQL 컨테이너 내부 접속
docker-compose exec mysql mysql -u petmatching_user -p petmatching

# Redis 컨테이너 내부 접속  
docker-compose exec redis redis-cli
```

### 로그 확인
```bash
# 실시간 로그 확인
docker-compose logs -f backend

# 마지막 100줄만 확인
docker-compose logs --tail=100 backend
```

### 컨테이너 디버깅
```bash
# 컨테이너 내부 접속
docker-compose exec backend bash
docker-compose exec frontend sh

# 컨테이너 리소스 사용량 확인
docker stats
```

## 🚨 문제 해결

### 포트 충돌
```bash
# 포트 사용 확인
netstat -an | grep :3000
netstat -an | grep :8080

# 또는 docker-compose.yml에서 포트 변경
```

### 메모리 부족
```bash
# Docker Desktop 메모리 설정 확인 (최소 4GB)
# 불필요한 컨테이너 정리
docker system prune -f
```

### 빌드 캐시 문제
```bash
# 캐시 없이 재빌드
docker-compose build --no-cache

# 전체 재시작
docker-compose down
docker-compose up --build
```

### 데이터베이스 초기화
```bash
# Volume 삭제 (데이터 손실 주의!)
docker-compose down -v
docker-compose up
```

## 📊 모니터링

### 서비스 상태 확인
- Backend Health: http://localhost:8080/actuator/health
- Redis Commander: http://localhost:8081

### 성능 모니터링
```bash
# 컨테이너 리소스 사용량
docker stats

# 로그 레벨 조정 (application-docker.yml)
logging:
  level:
    com.back: DEBUG  # 개발 시
    com.back: INFO   # 운영 시
```

## 🔒 보안 설정

### 환경 변수 관리
- `.env` 파일은 절대 Git에 커밋하지 않기
- `JWT_SECRET`은 256비트 이상의 안전한 키 사용
- MySQL 패스워드는 복잡한 패스워드 사용

### 네트워크 보안
- 컨테이너 간 통신은 Docker 네트워크 사용
- 외부 노출 포트 최소화
- 운영 환경에서는 SSL/TLS 적용

## 📚 추가 자료

- [Docker Compose 공식 문서](https://docs.docker.com/compose/)
- [Next.js Docker 가이드](https://nextjs.org/docs/deployment#docker-image)
- [Spring Boot Docker 가이드](https://spring.io/guides/gs/spring-boot-docker/)

---

💡 **팁**: 개발 중에는 `./scripts/dev-start.sh`를 사용하여 데이터베이스만 컨테이너로 실행하고, 
애플리케이션은 로컬에서 실행하는 것이 hot reload 등의 개발 편의성 면에서 좋습니다.