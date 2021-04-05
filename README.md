# Pygmy

Pygmy.com - the World’s smallest book store is two-tier web application using microservices at each tier.

Please note:
1. JDK and Maven is required on the host machine to run the run.sh script.
2. Logs for all test cases can be seen in the logs folder. Each microservice/client will have its own log file.
3. run.sh will start  microservices/cliens which will fire requests randomly for different items. This will happen for 120 seconds and then the script will kill all processes. To run the client for a longer duration, please change the value in run.sh file.

run.sh will take 2 parameters.

First parameter is mode. Values can be - all, catalog, order, frontend, client [Mode = all will start up all microservices and clients]

Second parameter is number of clients (optional): Default value is 3, this parameter will be considered in case of mode = all or mode = client


To deploy the code: 

1. CASE 1 - Single server, single buyer
    
    sh run.sh all 1

2. CASE 2 - Single server, N buyers

    sh run.sh all 3

3. CASE 3 - One server per microservice and N client nodes [AWS]
    
    sh run.sh catalog [SERVER 1]
   
    sh run.sh order [SERVER 2]
   
    sh run.sh frontend [SERVER 3]
   
    sh run.sh client 3 [SERVER 4]


AWS - EC2 instances
We have considered Linux AMI.

1. After cloning the code, open the hostname.conf file in root directory of project and set the hostname as PublicDNSName of EC2 instances for all three microservices.
2. Create a tar file of the complete source - 
   
    tar czf Pygmy.tar.gz Pygmy

Now for each sever, follow the steps to setup code on AWS and deploy it. (Replace key-path and Public-DNS-Name)

1. Copy the tar file created to the EC2 instance.

    scp -i key-path Pygmy.tar.gz ec2-user@Public-DNS-Name:/home/ec2-user

2. SSH on to the system.

    ssh -i key-path ec2-user@Public-DNS-Name

3. Change directory

    cd /home/ec2-user

4. Install JDK (if not already present)

    sudo yum install java-11-openjdk-devel (IMPORTANT: code must be compiled and run on java 11)

    To check the java and compiler version use below command and change the version if required to java 11:

    sudo alternatives --config java

    sudo alternatives --config javac

5. Install maven and set the location in path (if not already present)

    sudo wget https://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo

    sudo sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo

    sudo yum install -y apache-maven

6. Extract the contents of tar file and change directory

    tar -xvzf Pygmy.tar.gz

7. cd Pygmy/

8. Run run.sh with appropriate parameters as stated above

9. Check logs in the (microservice)/logs folder for server logs and in client/logs for client logs.