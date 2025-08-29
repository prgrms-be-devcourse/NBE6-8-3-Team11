#!/bin/bash

# PetMatching Local Development Environment Startup Script

set -e

echo "🚀 PetMatching 로컬 개발 환경을 시작합니다..."

# Check if .env file exists
if [ ! -f .env ]; then
    echo "📋 .env 파일이 없습니다. .env.example을 복사합니다..."
    cp .env.example .env
    echo "⚠️  .env 파일을 확인하고 필요한 값들을 수정해주세요."
fi

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker가 실행되지 않았습니다. Docker를 시작해주세요."
    exit 1
fi

# Check if Docker Compose is available
if ! command -v docker-compose > /dev/null 2>&1; then
    echo "❌ Docker Compose가 설치되지 않았습니다."
    exit 1
fi

echo "🛑 기존 컨테이너들을 중지합니다..."
docker-compose down

echo "🧹 사용하지 않는 이미지를 정리합니다..."
docker image prune -f

echo "📦 서비스들을 빌드하고 시작합니다..."
docker-compose up --build -d

echo "⏳ 서비스들이 시작될 때까지 기다립니다..."
sleep 30

# Health check
echo "🏥 서비스 헬스체크를 진행합니다..."

# Check MySQL
echo "📊 MySQL 연결 확인 중..."
for i in {1..30}; do
    if docker-compose exec -T mysql mysqladmin ping -h localhost -u root -p"${MYSQL_ROOT_PASSWORD:-rootpassword}" --silent; then
        echo "✅ MySQL이 준비되었습니다."
        break
    fi
    if [ $i -eq 30 ]; then
        echo "❌ MySQL 연결에 실패했습니다."
        docker-compose logs mysql
        exit 1
    fi
    echo "MySQL 시작 대기 중... ($i/30)"
    sleep 2
done

# Check Redis
echo "🔴 Redis 연결 확인 중..."
if docker-compose exec -T redis redis-cli ping | grep -q "PONG"; then
    echo "✅ Redis가 준비되었습니다."
else
    echo "❌ Redis 연결에 실패했습니다."
    docker-compose logs redis
    exit 1
fi

# Check Backend
echo "🔧 Backend API 확인 중..."
for i in {1..60}; do
    if curl -f -s http://localhost:8080/actuator/health > /dev/null; then
        echo "✅ Backend API가 준비되었습니다."
        break
    fi
    if [ $i -eq 60 ]; then
        echo "❌ Backend API 시작에 실패했습니다."
        docker-compose logs backend
        exit 1
    fi
    echo "Backend API 시작 대기 중... ($i/60)"
    sleep 3
done

# Check Frontend
echo "🌐 Frontend 확인 중..."
for i in {1..30}; do
    if curl -f -s http://localhost:3000 > /dev/null; then
        echo "✅ Frontend가 준비되었습니다."
        break
    fi
    if [ $i -eq 30 ]; then
        echo "❌ Frontend 시작에 실패했습니다."
        docker-compose logs frontend
        exit 1
    fi
    echo "Frontend 시작 대기 중... ($i/30)"
    sleep 2
done

echo ""
echo "🎉 모든 서비스가 성공적으로 시작되었습니다!"
echo ""
echo "📋 서비스 접속 정보:"
echo "   🌐 Frontend: http://localhost:3000"
echo "   🔧 Backend API: http://localhost:8080"
echo "   📊 Backend Health: http://localhost:8080/actuator/health"
echo "   🗄️  MySQL: localhost:3306"
echo "   🔴 Redis: localhost:6379"
echo "   🛠️  Redis Commander: http://localhost:8081 (Optional)"
echo ""
echo "📝 유용한 명령어:"
echo "   로그 확인: docker-compose logs -f [service-name]"
echo "   서비스 중지: docker-compose down"
echo "   서비스 재시작: docker-compose restart [service-name]"
echo "   컨테이너 상태: docker-compose ps"
echo ""
echo "🛠️  개발 준비 완료! 코딩을 시작하세요! 🚀"