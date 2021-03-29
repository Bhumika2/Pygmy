# Pygmy

Pygmy.com - the World’s smallest book store is two-tier web application using microservices at each tier.

Please note:
1. JDK and Maven is required on the host machine to run the run.sh script.
2. run.sh will start all microservices and start the client which will fire requests randomly for different items. This will happen for 20 seconds and then the script will kill all processes. To run the client for a longer duration, please change the value in run.sh file.
3. Logs for all test cases can be seen in the logs folder. Each microservice will have its own log file.
