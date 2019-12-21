PID=$(cat  /home/jenkins_home/workspace/backEnd/backEnd.pid)
kill -9 $PID
source /etc/profile
nohup java -jar /var/jenkins_home/workspace/backEnd/ДњТы/backend/target/buildBackEnd.jar --server.port=8085 >> /home/jenkins_home/workspace/backEnd/catalina.out 2>&1 &
echo "service started....."
echo $! >/home/jenkins_home/workspace/backEnd/up-admin.pid 
if [ -f /home/jenkins_home/workspace/backEnd/up-admin.pid  ]; then
     PID=$(cat /home/jenkins_home/workspace/backEnd/up-admin.pid)
     echo "start service success!"
     echo "PID is $PID"
  else
    echo "start service fail!"
fi