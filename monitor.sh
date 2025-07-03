#!/bin/bash
echo "📊 Fintech Microservices Monitoring"
echo "=================================="
echo "🐳 Container Status:"
docker-compose ps
echo ""
echo "🏥 Health Checks:"
services=("http://localhost:8080/actuator/health" "http://localhost:8081/actuator/health" "http://localhost:8082/actuator/health" "http://localhost:8083/actuator/health" "http://localhost:8761/actuator/health")

for url in "${services[@]}"; do
    if curl -f -s "$url" > /dev/null; then
        echo "✅ $url - UP"
    else
        echo "❌ $url - DOWN"
    fi
done
echo ""
echo "📋 Eureka Registered Services:"
curl -s http://localhost:8761/eureka/apps | grep -o '<name>[^<]*</name>' | sed 's/<name>//g' | sed 's/<\/name>//g' || echo "Could not fetch Eureka data"

echo ""
echo "📈 Quick Commands:"
echo "View logs: docker-compose logs -f [service-name]"
echo "Restart service: docker-compose restart [service-name]"
echo "Stop all: docker-compose down"
echo "Rebuild: docker-compose up --build -d"
