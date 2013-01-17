# gatakka


### G.A.T.E. and Akka together

#### Running instructions:

Starting the terminal input loop and cluster seeds

1. sbt/sbt
2. sbt> project client
3. sbt> run-main run-main Client 7373

From the terminal you can enter a sentence for ANNIE, 'q' for quit.

Starting the Gator workers
1. sbt/sbt
2. sbt> project gator
3. sbt> run-main Gator 54321

Currently you MUST use port 54321 or change the Client object, or application.conf to reflect a different port.
