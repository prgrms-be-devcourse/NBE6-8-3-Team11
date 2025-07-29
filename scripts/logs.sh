#!/bin/bash

# View logs for PetMatching services

if [ $# -eq 0 ]; then
    echo "📝 전체 서비스 로그를 확인합니다..."
    docker-compose logs -f
elif [ "$1" = "dev" ]; then
    echo "📝 개발 환경 로그를 확인합니다..."
    docker-compose -f docker-compose.dev.yml logs -f
else
    echo "📝 $1 서비스 로그를 확인합니다..."
    docker-compose logs -f "$1"
fi

echo ""
echo "사용법:"
echo "  ./scripts/logs.sh           # 전체 로그"
echo "  ./scripts/logs.sh backend   # 백엔드 로그만"  
echo "  ./scripts/logs.sh frontend  # 프론트엔드 로그만"
echo "  ./scripts/logs.sh mysql     # MySQL 로그만"
echo "  ./scripts/logs.sh dev       # 개발환경 로그"