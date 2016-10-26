
build: target/hello-world-webapp-0.1-SNAPSHOT-jar-with-dependencies.jar
	sudo docker build -t hello-world-webapp .

target/hello-world-webapp-0.1-SNAPSHOT-jar-with-dependencies.jar:
	mvn clean verify

# Runs the webapp in a docker container, using volume from tms-hg-agent
run-interactive:
	sudo docker rm hello-world || echo "ignoring docker rm error" && \
	sudo docker run --rm=true -i -t -p 4567 --pid host \
		--net host --volumes-from=tms-hg-agent --name hello-world \
		-v /docker/tmp:/tmp hello-world-webapp || echo done

run:
	sudo docker rm hello-world || echo "ignoring docker rm error" && \
	sudo docker run -d -p 4567 --pid host \
		--net host --volumes-from=tms-hg-agent --name hello-world \
		-v /docker/tmp:/tmp hello-world-webapp

stop:
	sudo docker stop hello-world

clean:
	rm -rf target
