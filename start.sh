#!/bin/bash
if [ ! -f /usr/local/siana/application-prod.yml ]; then
  echo '请先在/usr/local/siana/ 配置好application-prod.yml 文件在在运行该脚本' >&2
  exit 1
fi
echo "检查是否安装Git"
if ! [ -x "$(command -v git)" ]; then
  echo 'Git is not installed. Installing Git...' >&2
  echo "安装git"
  yum install git -y
  echo "拉取代码"
fi
if [ ! -d "/usr/local/GPT-WEB-JAVA" ]; then
git clone -b 2.0 https://github.com/a616567126/GPT-WEB-JAVA.git
fi
#判断是否有maven
if ! command -v mvn &> /dev/null
then
echo "Maven not found, installing..."
# 安装maven
wget https://dlcdn.apache.org/maven/maven-3/3.9.2/binaries/apache-maven-3.9.2-bin.tar.gz --no-check-certificate
tar -xvf apache-maven-3.9.2-bin.tar.gz
mv apache-maven-3.9.2 /usr/local/
# 配置环境变量
echo 'export PATH=$PATH:/usr/local/apache-maven-3.9.2/bin' >> /etc/profile
source /etc/profile
# 配置阿里云镜像
mkdir -p /usr/share/maven/conf
echo '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
https://maven.apache.org/xsd/settings-1.0.0.xsd">
<mirrors>
<mirror>
<id>alimaven</id>
<mirrorOf>central</mirrorOf>
<name>阿里云公共仓库</name>
<url>https://maven.aliyun.com/repository/central</url>
</mirror>
</mirrors>
</settings>' > /usr/share/maven/conf/settings.xml
echo "Maven installed."
fi
#判断是否有jdk1.8
if ! command -v java &> /dev/null; then
echo "Installing JDK 1.8..."
yum install java-1.8.0-openjdk* -y
echo "JDK 1.8 installed."
fi
#判断/usr/local/siana是否存在
if [ ! -d "/usr/local/siana" ]; then
mkdir /usr/local/siana
fi
#执行git pull
cd /usr/local/GPT-WEB-JAVA/
git pull
echo "Code pulled successfully."
#执行mvn
mvn clean && mvn compile && mvn install
echo "Build successful."
#复制文件到/usr/local/siana
cp $(cd $(dirname $0);pwd)/target/Intelligent-Bot-0.0.1-SNAPSHOT.jar /usr/local/siana/
echo "File copied to /usr/local/siana."
#判断/etc/systemd/system下是否有bot.service
if [ ! -f "/etc/systemd/system/bot.service" ]; then
echo '[Unit]
Description=bot

[Service]
ExecStart= /usr/bin/java -jar -Dfile.encoding=utf-8 /usr/local/siana/Intelligent-Bot-0.0.1-SNAPSHOT.jar --spring.config.location=/usr/local/siana/application-prod.yml

[Install]
WantedBy=multi-user.target' > /etc/systemd/system/bot.service
echo "Service created."
systemctl daemon-reload
#配置开机自启
systemctl enable bot
fi
#重启bot服务
systemctl restart bot
echo "Bot restarted."
journalctl -fu bot