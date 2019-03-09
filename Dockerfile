FROM jboss/wildfly:12.0.0.Final

ADD ./target/lotteryAgent.war /opt/jboss/wildfly/standalone/deployments/
#ENV LOTTERY_BOSS_HOST localhost
#ENV LOTTERY_BOSS_PORT 8080
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]
