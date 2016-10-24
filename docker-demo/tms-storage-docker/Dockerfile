# Copyright 2012-2016 Red Hat, Inc.
#
# This file is part of Thermostat.
#
# Thermostat is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published
# by the Free Software Foundation; either version 2, or (at your
# option) any later version.
#
# Thermostat is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Thermostat; see the file COPYING.  If not see
# <http://www.gnu.org/licenses/>.
#
# Linking this code with other modules is making a combined work
# based on this code.  Thus, the terms and conditions of the GNU
# General Public License cover the whole combination.
#
# As a special exception, the copyright holders of this code give
# you permission to link this code with independent modules to
# produce an executable, regardless of the license terms of these
# independent modules, and to copy and distribute the resulting
# executable under terms of your choice, provided that you also
# meet, for each linked independent module, the terms and conditions
# of the license of that module.  An independent module is a module
# which is not derived from or based on this code.  If you modify
# this code, you may extend this exception to your version of the
# library, but you are not obligated to do so.  If you do not wish
# to do so, delete this exception statement from your version.

FROM fedora:24
# Thermostat Storage Image.
#
# Environment:
#  * $THERMOSTAT_AGENT_USERNAME  - User name for thermostat agents to use
#                                  for connecting to storage.
#  * $THERMOSTAT_AGENT_PASSWORD  - Password for thermostat agents to use
#                                  for connecting to storage.
#  * $THERMOSTAT_CLIENT_USERNAME - User name for thermostat clients to
#                                  use for connecting to storage
#  * $THERMOSTAT_CLIENT_PASSWORD - PASSWORD for thermostat clients to
#                                  use for connecting to storage
#  * $THERMOSTAT_DB_PORT         - The port to listen on
#  * $MONGO_USERNAME             - User name to connect to the mongodb backend
#  * $MONGO_PASSWORD             - Password to connect to the mongodb backend
#  * $MONGO_URL                  - The mongodb url to connect to


LABEL Name="thermostat-19-storage-docker" \
      Version="1.99" \ 
      Release="1.0" \
      Architecture="x86_64"

RUN dnf install -y mercurial maven mongodb mongodb-server java-1.8.0-openjdk-devel libsecret-devel make gcc gtk2-devel autoconf automake libtool procps-ng passwd --setopt=deltarpm=0 && dnf clean all

RUN dnf update -y --setopt=deltarpm=0 && dnf clean all

ENV THERMOSTAT_VERSION=1.99 \
    HOME=/root \
    USER=root

ENV THERMOSTAT_HOME=/root/thermostat/distribution/target/image
ENV THERMOSTAT_DB_PORT=8999
ENV USER_THERMOSTAT_HOME=/root/.thermostat
EXPOSE ${THERMOSTAT_DB_PORT}

#COPY __tms__ /root/thermostat
RUN cd /root && hg clone http://icedtea.classpath.org/hg/thermostat/
RUN cd /root/thermostat && mvn -Dmaven.test.skip=true clean integration-test

ADD root /

ENTRYPOINT ["container-entrypoint"]
CMD [ "run-thermostat-storage" ]
