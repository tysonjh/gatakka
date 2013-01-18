# gatakka
***

### G.A.T.E. and Akka together
An Akka cluster system with a client, cluster aware routers and G.A.T.E. processors.

### Current status
2013/01/18 - Akka is up and working in a primitive form, still no presence of GATE beyond the jars.

### Running instructions:
1. Start the client
  The client will start up the Akka cluster and provide a terminal for commands. Enter `q` to quit,
  or enter a sentence for ANNIE to process.

2. Start at least one Gator
  Gator will start a router and 3 GATE workers by default.

#### Client

1. sbt/sbt
2. sbt> project client
3. sbt> run-main gatakka.Client

Once the client is running enter sentences into the terminal for GATE to process, or `q` to quit.

#### Gator (GATE Router / Workers)

1. sbt/sbt
2. sbt> project gator
3. sbt> run-main gatakka.Gator

#### Logging
SLF4J with Logback is used for logging into the project directory. The client will write into client.log and the
workers will write into gator.log. By default it is very verbose with DEBUG logging levels. If you are not so
interested in seeing cluster membership and actor LoggingReceive style messages change the log level to INFO.

