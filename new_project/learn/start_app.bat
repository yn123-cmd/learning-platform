@echo off
echo Starting Spring Boot Application...
java -Dspring.profiles.active=default -cp "target/classes;target/lib/*" org.example.learn.LearnApplication
pause 