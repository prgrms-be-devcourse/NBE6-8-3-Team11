#!/bin/bash

echo "🔍 서비스 상태 확인 중..."

echo "📊 컨테이너 상태:"
docker-compose ps

echo ""
echo "🗄️ MySQL 연결 테스트:"
docker-compose exec -T mysql mysqladmin ping -h localhost -u root -p"${MYSQL_ROOT_PASSWORD:-rootpassword}" 2>/dev/null && echo "✅ MySQL 연결 성공" || echo "❌ MySQL 연결 실패"

echo ""
echo "🔴 Redis 연결 테스트:"
docker-compose exec -T redis redis-cli ping 2>/dev/null && echo "✅ Redis 연결 성공" || echo "❌ Redis 연결 실패"

echo ""
echo "🔧 Backend 로그 (최근 20줄):"
docker-compose logs backend --tail=20

echo ""
echo "🌐 Frontend 로그 (최근 10줄):"
docker-compose logs frontend --tail=10