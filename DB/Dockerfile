FROM mysql:8.3.0

ENV MYSQL_ROOT_PASSWORD=password

ENV TZ=Asia/Taipei

ADD *.sql /docker-entrypoint-initdb.d

EXPOSE 3306