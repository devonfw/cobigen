Sample Application [![Build Status](https://travis-ci.org/oasp/oasp4js.svg?branch=development)](https://travis-ci.org/oasp/oasp4js) [![Coverage Status](https://coveralls.io/repos/oasp/oasp4js/badge.svg?branch=development)](https://coveralls.io/r/oasp/oasp4js?branch=development)                                                                                                                
===
 
The sample application is built on top of the [application template](https://github.com/oasp/oasp4js-app-template) and is an AngularJS application. Some of the AngularJS modules (e.g. oasp-security, oasp-i18n, etc.) which can be reused in your application are available in [this repository](https://github.com/oasp/oasp4js-bower) either directly or using the Bower package manager. The sample application needs the [oasp4j](https://github.com/oasp/oasp4j) (Java) server part to be installed.

Getting Started
---

To get started you need to clone the oasp4j repository containing the server part, build and deploy it on a Tomcat server. To get started with the client part you need to clone the oasp4js repository, build the client and start it.

Install prerequisites
---

You need a Git client to clone the repositories and the Node.js platform (including its package manager - npm) which allows Gulp and Bower to install the dependencies and build the application. [Here](https://github.com/oasp/oasp4js/wiki/Prerequisites) you can learn how to install the prerequisites. 
Also, for the server part you need Maven (required version: 3.x) and Tomcat (min. version: 7.x, recommended version: 8.x) to be installed. For installation details please refer to the [Tomcat's](http://tomcat.apache.org/download-80.cgi) and [Maven's](http://maven.apache.org/download.cgi) home pages. 

Create the `<oasp_dir>` directory for the sample application
---

```
mkdir <oasp_dir>
cd <oasp_dir>    
```

Set up the server part of the application
---

Clone the oasp4j repository:

```  
git clone https://github.com/oasp/oasp4j.git
```

Let Maven build the server part:

```  
cd oasp4j
mvn clean install 
```

After a successful build, the `oasp4j-sample-server.war` file should appear in the following directory: 

``` 
<oasp_dir>\oasp4j\oasp4j-samples\oasp4j-sample-server\target

```

Assuming you have installed the Tomcat in the `<tomcat_dir>` directory, copy the 'oasp4j-sample-server.war' file to 

```
<tomcat_dir>\webapps
```

Edit the `<tomcat_dir>\conf\tomcat-users.xml` file. Add the following roles and users to it: 

```xml
<tomcat-users>
  <user username="chief" password="chief" />
  <user username="waiter" password="waiter" />
  <user username="barkeeper" password="barkeeper" />
  <user username="cook" password="cook" />
</tomcat-users>
```

<a name="changeTomcatsPortInServerXml"></a>
Edit the `<tomcat_dir>\conf\server.xml` file. Change the Tomcat port to 8081 (if you don't want to change the Tomcat's port, then see the _[hint about how to configure a different port on the client side](#howToChangeTomcatsPortInConfigJson)_): 

```xml
<Connector connectionTimeout="20000" port="8081" protocol="HTTP/1.1" redirectPort="8443"/>
```

Set up the server part's configuration. Under `<tomcat_dir>\lib` create the `config\env` directories:
 
```
cd <tomcat_dir>\lib
mkdir config\env
```

Create the `application.properties` file, put it under `<tomcat_dir>\lib\config\env` and add the following content to it: 

```ini
database.user.login = sa
database.user.password =
database.url = jdbc:h2:~/restaurant-db;INIT=create schema if not exists public
database.migration.auto = true
database.migration.clean = true
```
_Hint: The value of the `database.user.password` property should be left empty, exactly as shown above._

Start the Tomcat: 

```
<tomcat_dir>\bin\startup.bat 
```

Set up the client part of the application
---

Go back to the `<oasp_dir>` directory: 

```
cd ..
```

Clone the oasp4js repository:

``` 
git clone https://github.com/oasp/oasp4js.git 
```

Install the client part's dependencies: 

```
cd oasp4js
npm install
```

<a name="howToChangeTomcatsPortInConfigJson"></a> 
_**Hint about how to configure a different Tomcat's port to be used on the client side:** If you did not want to change the Tomcat's port to 8081 as [described here](#changeTomcatsPortInServerXml), then you have to change the default port which is configured in the client part of the application, in the `<oasp_dir>\oasp4js\config.json` file. You have to set the correct port number in the following line of the aforementioned configuration file (replace '8888' with the port number which you configured for your Tomcat):_

```
"proxy": "http://localhost:8888",
```

Start the application using Gulp:

``` 
gulp serve
```

The above Gulp's task opens the client part of the application in your default browser and watches for any changes in HTML/JavaScript/CSS files. Once you change one, the page is reloaded automatically!
 
You can sign in using the following credentials: waiter/waiter or cook/cook.
