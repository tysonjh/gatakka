# gatakka
***

### G.A.T.E. and Akka together
An Akka cluster system with a client, cluster aware routers and G.A.T.E. processors.

### Running instructions:
1. Start the client
  The client will start up the Akka cluster and provide a terminal for commands. Enter `q` to quit,
  or enter a sentence for ANNIE to process.

2. Start at least one Gator
  Gator will start a router and single GATE worker.

3. Optionally start multiple GatorWorker
  GatorWorker joins the routers providing more workers for processing jobs.

#### Client

1. sbt/sbt
2. sbt> project client
3. sbt> run-main Client 7373

Once the client is running you can enter sentences into the terminal for GATE to process, or `q` to quit.

#### Gator (GATE Router / Workers)

1. sbt/sbt
2. sbt> project gator
3. sbt> run-main Gator

#### Workers

1. sbt/sbt
2. sbt> project gator
3. sbt> run-main GatorWorker
