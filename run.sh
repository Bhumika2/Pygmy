#Single script to set up microservices, run the system and destroy the system

#Run microservice catalog

echo "Starting up Catalog"
cd catalog
> logs/catalog.log
mvn clean install > logs/catalog.log
nohup mvn ninja:run > logs/catalog.log &
cd ../

#Run microservice order

echo "Starting up Order"
cd order
> logs/order.log
mvn clean install > logs/order.log
nohup mvn ninja:run > logs/order.log &
cd ../

#Run microservice frontend

echo "Starting up Frontend"
cd frontend
> logs/frontend.log
mvn clean install > logs/frontend.log
nohup mvn ninja:run > logs/frontend.log &
cd ../

sleep 10


echo "Starting up client"
>client_log.log
javac Client.java
java Client > client_log.log &

echo "Client will run for 20 seconds - value can be changed in run.sh file"
sleep 20

echo "Stopping client"
kill $(ps aux | grep '[C]lient' | awk '{print $2}')

echo "Destroying all microservices"
kill $(ps aux | grep '[n]inja' | awk '{print $2}')
