gatakka
========

G.A.T.E. and Akka together

Running instructions:

Starting the terminal input loop and cluster seeds
1. sbt/sbt
2. sbt> project luster
3. sbt> run-main run-main TerminalController 7373

From here you can enter a sentence for ANNIE, 'q' for quit.


Starting the Gator workers
1. sbt/sbt
2. sbt> project gator
3. sbt> run-main Gator
