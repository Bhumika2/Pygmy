#Single script to set up microservices, run the system and destroy the system

if [ $# -eq 0 ]
then
	echo "Incorrect number of arguments passed"
	exit
fi

mode=$1
if [ $mode = "all" ]
then 
	echo "Mode = all, Running all microservices - catalog, order and frontend and starting up clients (default 3)"
else 
	echo "Mode = $mode, Starting up with only the desired server/client"
fi


serverUp="false"
clientUp="false"

if [ $mode = "all" ] || [ $mode = "catalog" ]
then 
  	#Run microservice catalog
	echo "Starting up Catalog"
	serverUp="true"
	cd catalog
	> logs/catalog.log
	mvn clean install > logs/catalog.log
	nohup mvn ninja:run > logs/catalog.log &
	cd ../
fi

if [ $mode = "all" ] || [ $mode = "order" ]
then
  	#Run microservice order
	echo "Starting up Order"
	serverUp="true"
	cd order
	> logs/order.log
	mvn clean install > logs/order.log
	nohup mvn ninja:run > logs/order.log &
	cd ../
fi

if [ $mode = "all" ] || [ $mode = "frontend" ]
then
	#Run microservice frontend
	echo "Starting up Frontend"
	serverUp="true"
	cd frontend
	> logs/frontend.log
	mvn clean install > logs/frontend.log
	nohup mvn ninja:run > logs/frontend.log &
	cd ../
fi

if [ $mode = "all" ] || [ $mode = "client" ]
then 
	N=3
	if [ $# -eq 2 ]
	then
		N=$2 
	fi
	echo "Starting up $N client(s)"
	clientUp="true"

	javac Client.java

	for ((i=1; i<= $N; i++))
	do
	  >client_$i.log
	  java Client > client_$i.log &
	  echo "Client $i started"
	done
fi

echo "Process will run for 20 seconds - value can be changed in run.sh file"
sleep 20

if [ $clientUp = "true" ]
then
	echo "Stopping client(s)"
	kill $(ps aux | grep '[C]lient' | awk '{print $2}')
fi

if [ $serverUp = "true" ]
then
	echo "Destroying all microservices"
	kill $(ps aux | grep '[n]inja' | awk '{print $2}')
fi