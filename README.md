# oe-system-three-reference 
## Quick start guide

### Properties Configuration
<a name="properties"></a>Make sure you have `oe-system-three-reference.properties` located in your home directory with the app local config.

Example config file to override the app's default configuration:
```
GRAYLOG_ENABLED=false  # Mandatory to run application with Vagrant
GRAYLOG_HOSTNAME=      # Mandatory to run application with Vagrant
GRAYLOG_PORT=12206     # Mandatory to run application with Vagrant
jdbc.driver=org.postgresql.Driver
jdbc.username=****     # DB username
jdbc.password=****     # DB password
jdbc.url=jdbc:postgresql://postgres.dev.openenglish.com:5432/oe_system_three_reference # DB URL
liquibase.contexts=dev
connection.pool.maxWait=5000
connection.pool.initialSize=1
connection.pool.maxActive=10
```

### Database Setup
Before running the app for the first time, create a new database and set its URL and credentials on 
the application's properties file. Next, define the schema by running the following commands:
```
cd oe-system-three-reference-model
mvn process-resources -Pdatabase-create
```
This will run the liquibase scripts required to define the schema. Any time you modify data in the 
tables and want a clean start, you can run that command again to recreate the database.

#### Update Database
To apply new liquibase scripts to the database execute the following commands:
```
cd oe-system-three-reference-model
mvn process-resources -Pdatabase-update
```

### Application Status Information
Test your setup running locally with the following endpoints:

* Info endpoint will provide manifest information, including JDK version and last git commit.
* Health endpoint will check the status of different components this app depends on. If any of its 
components is down, the whole app will be flagged as being down.
* Monitoring endpoint for Java Melody information.
* Swagger documentation endpoint.

```
curl -i http://localhost:8090/actuator/info
curl -i http://localhost:8090/actuator/health
curl -i http://localhost:8090/monitoring
curl -i http://localhost:8090/api
```

### Running with Vagrant

1. Update your `/etc/hosts` file with the following line:
    ```
    127.0.0.1	oe-system-three-reference.dev.openenglish.com
    ```
2. Make sure you have `oe-system-three-reference.properties` located in your home directory with the app local config.
3. Open a terminal and run the following steps:
    ```
    cd $devRoot/oe-system-three-reference
    mvn clean install -DskipTests -Denforcer.skip=true -Prelease
    cd oe-system-three-reference/Vagrant
    vagrant up
    vagrant ssh # This will connect to the vagrant VM using ssh
    sh /vagrant/madness.sh
    ```
4. Test your setup from your local terminal (not the vagrant ssh connection)
    ```
    curl -L -k -i http://oe-system-three-reference.dev.openenglish.com:8790/health
    ```
5. If you update your properties files make sure you update it in Vagrant as well. Connect with ssh 
to Vagrant and run the following command before running madness.sh:
    ```
    sudo cp user-home/oe-system-three-reference.properties /opt/open-english/setup/dev/oe-system-three-reference.properties
    ```
