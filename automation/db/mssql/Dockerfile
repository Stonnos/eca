FROM mcr.microsoft.com/mssql/server:2017-CU17-ubuntu

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

COPY . /usr/src/app

# Grant all permissions for the init script to be executable
RUN chmod +x /usr/src/app/init-script.sh

CMD /bin/bash ./entrypoint.sh