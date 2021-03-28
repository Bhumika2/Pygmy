#Single script to set up microservices, run the system and destroy the system

#Run microservice catalog

echo "Starting up Catalog"
cd catalog
mvn clean install > nohup.out
nohup mvn ninja:run &
cd ../

#Run microservice order

echo "Starting up Order"
cd order
mvn clean install > nohup.out
nohup mvn ninja:run &
cd ../

#Run microservice frontend

echo "Starting up Frontend"
cd frontend
mvn clean install > nohup.out
nohup mvn ninja:run &
cd ../

sleep 5


echo "Starting up client"
javac Client.java
java Client > client.out &

sleep 10

echo "Stopping client"
kill $(ps aux | grep '[C]lient' | awk '{print $2}')

echo "Destroying all microservices"
kill $(ps aux | grep '[n]inja' | awk '{print $2}')
