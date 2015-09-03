#pp-service Vagrant
##Quick start guide

For an even quicker quickstart: https://openenglish.hackpad.com/PP-Service-Madness-wvSq0gzVHo2

Update your `/etc/hosts` file with the following (resolve most OE DNS entries to `127.0.0.1`):
```
127.0.0.1	postgres.dev.openenglish.com
127.0.0.1	pp-service.dev.openenglish.com
```

<a name="properties"></a>Make sure you have `pp-service.properties` located in your home directory,
 it should look like this (with your local postgres connection info), note the `jdbc.url` value:

```
jdbc.driver=org.postgresql.Driver
jdbc.url=jdbc:postgresql://postgres.dev.openenglish.com/payment_platform
jdbc.username=<<your login>>
jdbc.password=<<your password>>
jdbc.driver=org.postgresql.Driver
liquibase.contexts=dev
```

<a name="steps"></a>Here is what you need to do:

1. `cd $devRoot/pp-service`
2. `mvn clean install -DskipTests -Denforcer.skip=true -Prelease`
3. `cd pp-service-webapp/Vagrant`
4. `vagrant up`
5. `vagrant ssh`
6. `cd target`
7. `sudo dpkg -i pp-service-webapp_xxx.deb`

Test your setup:

##Health check
```
curl -i http://pp-service.dev.openenglish.com:8780/pp-service/ping
```

```

##Supporting Documentation

For more information on the `curl` command above and how we are using this, see this Xavier documentation: 
https://openenglish.jira.com/wiki/pages/viewpage.action?pageId=81592472

#Detailed Instructions

##Getting Started

The first time you start Vagrant will be a little different than normal. First, you'll need to 
have the `target` directory in `pp-service-webapp` created (this is done by maven) and Vagrant
will need to download the VM. The VM download is only done once, so the initial startup of Vagrant 
takes a bit. Subsequent starts of the VM are very quick.

You will need to build your project so you have a `WAR` file and a `deb` package. Also, you 
need to have the `target` directory created in `pp-service/pp-service-webapp/` in order
for Vagrant to start. Once you have your `target` directory and your `deb` file, you can use
Vagrant to simulate a production environment.

To get the environment setup and running, follow the [steps listed](#steps) above.

##Domain names

We are moving to a set of standardized domain names at Open English, this means all systems
in production will use the format: `[system].openenglish.com`. So pp-service would be:
`pp-service.openenglish.com`. For other environments, say `dev` or `stg` you would use this format:
`pp-service.stg.openenglish.com` and `pp-service.dev.openenglish.com`.
 
With this in mind, the VM created here for pp-service will have this `/etc/hosts` file:

* `10.0.2.2  postgres.dev.openenglish.com`

The new system will require you to update your `/etc/hosts` file from time-to-time. It's 
important to remember which DNS entries you have in your `/etc/hosts` file, if you find 
you cannot access a system, you might want to check your `hosts` file to make sure you have
not mapped the DNS entry to your local box instead of hitting prod or stg.

##Database connections

To connect to the database, the VM needs access to YOUR database settings. The VM will use the 
version of Postgres installed on your local machine, or another instance of Postgres, either way
the VM will need acceess to your `~/pp-service.properties` file. The contents of the file
[should be similar to the file above](#properties).
 
How do we get your `~/pp-service.properties` file into the VM? If you look at the `Vagrant` file
located in this directory you'll see these lines:

```
pp-service.vm.synced_folder "../target/", "/home/vagrant/target"
pp-service.vm.synced_folder "~", "/home/vagrant/user-home"
```

These two lines, create mount points in the VM. The above entries tell you the `target` directory 
in `pp-service-webapp` is mounted to `/home/vagrant/target` in the VM and that your host OS
home directory `~` is mounted to `/home/vagrant/user-home`. Now that your home directory is available
to the VM, we can just copy your `~/pp-service.properties` in the VM.

##Standard directory layout

The VM created here is customized to be as close to production as possible. As a result, there are 
certain files and directories that we simulate here, files and directories that the deploy process
installs when deploying your `deb` files to production. Here is the `oe standard file layout`:

```
/opt/open-english/
/opt/open-english/setup
/opt/open-english/setup/CONFIGDATA
/opt/open-english/setup/dev
/opt/open-english/setup/dev/pp-service.properties
```

All OE boxes that follow the deploy process, will have an `/opt/open-english` directory. The `setup` 
directory is how the `deb` installer knows which environment you are using, `dev|stg|prod`. Here
is the contents of the `CONFIGDATA` file:

```
groupId com.openenglish.pp
artifactId pp-service
version FIXED-SNAPSHOT
env dev
```

The `/opt/open-english/setup/dev` directory matches the `env dev` name at the end of the `CONFIGDATA`
file. This path is used by [`EnvironmentPropertyConfigurer`](https://github.com/openenglish/oe-substrate/blob/master/src/main/java/com/openenglish/substrate/environment/EnvironmentPropertyConfigurer.java#L112-119)
to load env specific JDBC connection data. When the VM is started, your `~/pp-service.properties` file
is copied to `/opt/open-english/setup/dev/pp-service.properties`. This file is then read by
`pp-service` at startup and a connect made to your database.

 
##Log files

The log files for `pp-service` can be found in the `/opt/open-english/pp-service/logs` directory.
The log4j|slf4j|etc log file is `/opt/open-english/pp-service/logs/pp-service.log`. If you want
to see output from the app, just tail this file:

1. `cd $devRoot/pp-service/pp-service-webapp/Vagrant`
2. `vagrant ssh`
3. `tail -f /opt/open-english/pp-service/logs/pp-service.log`
 
If you want to tweak the log level, update the `logback.xml` config file:

1. `vi /opt/open-english/pp-service/config/logback.xml`

You will need to restart pp-service to have the config changes loaded

1. `sudo service pp-service stop
    sudo service pp-service start`


