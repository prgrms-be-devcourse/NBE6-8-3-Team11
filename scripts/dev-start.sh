#!/bin/bash

# Development environment - only databases, run backend/frontend locally

set -e

echo "🛠️  개발 환경을 시작합니다 (DB만, 앱은 로컬 실행)..."

# Start only databases
docker-compose -f docker-compose.dev.yml up -d

echo "⏳ 데이터베이스 서비스 대기 중..."
sleep 10

# Health check for databases
echo "📊 MySQL 연결 확인..."
for i in {1..20}; do
    if docker-compose -f docker-compose.dev.yml exec -T mysql mysqladmin ping -h localhost -u root -p"${MYSQL_ROOT_PASSWORD:-devpassword}" --silent; then
        echo "✅ MySQL 준비 완료"
        break
    fi
    if [ $i -eq 20 ]; then
        echo "❌ MySQL 연결 실패"
        exit 1
    fi
    sleep 2
done

echo "🔴 Redis 연결 확인..."
if docker-compose -f docker-compose.dev.yml exec -T redis redis-cli ping | grep -q "PONG"; then
    echo "✅ Redis 준비 완료"
else
    echo "❌ Redis 연결 실패"
    exit 1
fi

echo ""
echo "🎉 개발 환경 준비 완료!"
echo ""
echo "📋 다음 명령어로 애플리케이션을 시작하세요:"
echo ""
echo "🔧 Backend (별도 터미널):"
echo "   cd backend"
echo "   ./gradlew bootRun --args='--spring.profiles.active=dev'"
echo ""
echo "🌐 Frontend (별도 터미널):"  
echo "   cd frontend"
echo "   npm run dev"
echo ""
echo "📊 접속 정보:"
echo "   🗄️  MySQL: localhost:3306"
echo "   🔴 Redis: localhost:6379"
echo "   🛠️  Redis Commander: http://localhost:8081"
echo ""