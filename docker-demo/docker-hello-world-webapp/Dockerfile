FROM java:jdk
MAINTAINER Omair Majid <omajid@redhat.com>

ENV WEBAPP_PORT 4567

EXPOSE $WEBAPP_PORT

COPY target /usr/local/share/myapp
COPY docker-startup.sh /usr/local/share/myapp/

WORKDIR /usr/local/share/myapp

CMD [ "./docker-startup.sh" ]
