#!/bin/bash

# Stop all PetMatching services

echo "🛑 PetMatching 서비스들을 중지합니다..."

# Stop production environment
echo "Production 환경 중지 중..."
docker-compose down

# Stop development environment  
echo "Development 환경 중지 중..."
docker-compose -f docker-compose.dev.yml down

# Optional: Remove volumes (uncomment if needed)
# echo "⚠️  모든 데이터를 삭제합니다..."
# docker-compose down -v
# docker-compose -f docker-compose.dev.yml down -v

echo "✅ 모든 서비스가 중지되었습니다."