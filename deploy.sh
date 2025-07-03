#!bin/bash
echo "🚀 Starting Fintech Microservices Deployment..."
echo "📦 Building JAR files..."
services=("authentication-service" "api-gateway" "profile-service" "lending-engine" "eureka-server")
for service in "${services[@]}"; do
    if [ -d "$service" ]; then
        echo "Building $service..."
        cd $service
        mvn clean package -DskipTests
        cd ..
    else
        echo "⚠️  Service directory $service not found!"
    fi
done
echo "🐳 Starting Docker containers..."
docker-compose down
docker-compose up --build -d

echo "⏳ Waiting for services to start..."
sleep 30
echo "🔍 Checking service status..."
docker-compose ps
echo ""
echo "🌐 Service URLs:"
echo "📊 API Gateway: http://localhost:8080"
echo "🔐 Auth Service: http://localhost:8081"
echo "👤 Profile Service: http://localhost:8082" 
echo "💰 Lending Engine: http://localhost:8083"
echo "🗂️  Eureka Dashboard: http://localhost:8761"
echo "🐰 RabbitMQ Management: http://localhost:15673 (admin/admin123)"
echo "🗄️  MySQL: localhost:3307 (fintech_user/fintech_pass)"
echo ""
echo "✅ Deployment complete!"
