No Metrics comming out of agroal.

Start DB:
docker run -it --rm=true --name quarkus_test -e POSTGRES_USER=quarkus_test -e POSTGRES_PASSWORD=quarkus_test -e POSTGRES_DB=quarkus_test -p 5432:5432 postgres:14.1

Further stimulate DB connection:
http://localhost:8080/fruits

Check endpoint:
http://localhost:8080/q/metrics
