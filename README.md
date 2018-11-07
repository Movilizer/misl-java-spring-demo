# Movilizer MISL Java Example

This is an example of Java Springboot application that uses the exposed MISL APIs to connect to MISL Service Proxy

## Running and testing

Use docker compose to run the MISL Services Mock

```bash
cd mock
docker-compose up
```

When running the app set the environment variable:

```bash
export MISL_SERVICE=localhost:10000
```

When you run the app, try [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

The Service mock can be used to test multiple MISL components (just one instance is safe to serve them)