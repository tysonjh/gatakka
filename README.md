# gatakka
***

### G.A.T.E. and Akka together
An Akka cluster system with a client, cluster aware routers and G.A.T.E. processors.

### Current status
2013/01/21 - Akka is up and working. GATE is up and working.

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

#### Sample session
```
>run-main gatakka.Client
...
Toronto is a beautiful city, but Tyson finds it cold.
 Sending to gator
 Result received from actor $a:
  Start    End                 Type                Value
     23     27                Token                 city
     27     28                Token                    ,
     28     29           SpaceToken
     29     32                Token                  but
     11     12                Token                    a
     12     13           SpaceToken
     13     22                Token            beautiful
     22     23           SpaceToken
     44     45           SpaceToken
     45     47                Token                   it
     47     48           SpaceToken
     48     52                Token                 cold
     32     33           SpaceToken
     33     38                Token                Tyson
     38     39           SpaceToken
     39     44                Token                finds
      0     53             Sentence Toronto is a beautiful city, but Tyson finds it cold.
     52     53                Split                    .
     33     38          FirstPerson                Tyson
      0      7               Lookup              Toronto
     52     53                Token                    .
     33     38               Lookup                Tyson
     23     27               Lookup                 city
      0      7             Location              Toronto
     33     38               Person                Tyson
     10     11           SpaceToken
      8     10                Token                   is
      7      8           SpaceToken
      0      7                Token              Toronto
```

