#!/bin/bash

echo -e "Running SP-PIFO evaluation using run.sh"

# Compile


#/* Figure 5: SP-PIFO performance (uniform rank distribution) */

java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/AIFO.properties
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/PIFOOUR.properties
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/PCQ.properties 
                 #11
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/11/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/11/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/11/OEPSSIMPLE.properties 
                 #12
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/12/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/12/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/12/OEPSSIMPLE.properties 
                 #15
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/15/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/15/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/15/OEPSSIMPLE.properties 
                 #21
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/21/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/21/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/21/OEPSSIMPLE.properties 
         #22
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/22/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/22/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/22/OEPSSIMPLE.properties 
         #25
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/25/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/25/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/25/OEPSSIMPLE.properties 
         #51
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/51/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/51/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/51/OEPSSIMPLE.properties 
         #52
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/52/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/52/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/52/OEPSSIMPLE.properties 
         #55
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/55/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/55/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/55/OEPSSIMPLE.properties 

         #True
         #2Queue
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/PCQ.properties 
         #11
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/11/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/11/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/11/OEPSSIMPLE.properties 
         #12
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/12/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/12/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/12/OEPSSIMPLE.properties 
         #15
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/15/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/15/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/15/OEPSSIMPLE.properties 
         #21
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/21/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/21/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/21/OEPSSIMPLE.properties 
         #22
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/22/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/22/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/22/OEPSSIMPLE.properties 
         #25
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/25/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/25/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/25/OEPSSIMPLE.properties 
         #51
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/51/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/51/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/51/OEPSSIMPLE.properties 
         #52
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/52/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/52/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/52/OEPSSIMPLE.properties 
         #55
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/55/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/55/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_True/55/OEPSSIMPLE.properties 


         #2Queue
         #False
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/PCQ.properties 
         #11
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/11/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/11/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/11/OEPSSIMPLE.properties 
         #12
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/12/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/12/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/12/OEPSSIMPLE.properties 
         #15
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/15/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/15/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/15/OEPSSIMPLE.properties 
         #21
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/21/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/21/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/21/OEPSSIMPLE.properties 
         #22
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/22/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/22/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/22/OEPSSIMPLE.properties 
         #25
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/25/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/25/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/25/OEPSSIMPLE.properties 
         #51
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/51/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/51/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/51/OEPSSIMPLE.properties 
         #52
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/52/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/52/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/52/OEPSSIMPLE.properties 
         #55
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/55/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/55/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/2Queue_False/55/OEPSSIMPLE.properties 

         #4Queue
         #True
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/PCQ.properties 
         #11
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/11/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/11/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/11/OEPSSIMPLE.properties 
         #12
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/12/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/12/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/12/OEPSSIMPLE.properties 
         #15
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/15/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/15/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/15/OEPSSIMPLE.properties 
         #21
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/21/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/21/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/21/OEPSSIMPLE.properties 
         #22
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/22/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/22/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/22/OEPSSIMPLE.properties 
         #25
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/25/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/25/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/25/OEPSSIMPLE.properties 
         #51
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/51/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/51/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/51/OEPSSIMPLE.properties 
         #52
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/52/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/52/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/52/OEPSSIMPLE.properties 
         #55
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/55/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/55/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_True/55/OEPSSIMPLE.properties 

         #False
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/PCQ.properties 
         #11
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/11/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/11/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/11/OEPSSIMPLE.properties 
         #12
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/12/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/12/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/12/OEPSSIMPLE.properties 
         #15
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/15/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/15/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/15/OEPSSIMPLE.properties 
         #21
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/21/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/21/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/21/OEPSSIMPLE.properties 
         #22
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/22/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/22/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/22/OEPSSIMPLE.properties 
         #25
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/25/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/25/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/25/OEPSSIMPLE.properties 
         #51
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/51/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/51/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/51/OEPSSIMPLE.properties 
         #52
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/52/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/52/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/52/OEPSSIMPLE.properties 
         #55
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/55/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/55/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/4Queue_False/55/OEPSSIMPLE.properties 


         #8Queue
         #True
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/PCQ.properties 
         #11
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/11/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/11/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/11/OEPSSIMPLE.properties 
         #12
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/12/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/12/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/12/OEPSSIMPLE.properties 
         #15
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/15/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/15/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/15/OEPSSIMPLE.properties 
         #21
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/21/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/21/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/21/OEPSSIMPLE.properties 
         #22
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/22/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/22/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/22/OEPSSIMPLE.properties 
         #25
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/25/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/25/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/25/OEPSSIMPLE.properties 
         #51
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/51/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/51/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/51/OEPSSIMPLE.properties 
         #52
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/52/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/52/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/52/OEPSSIMPLE.properties 
         #55
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/55/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/55/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_True/55/OEPSSIMPLE.properties 

         #False
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/PCQ.properties 
         #11
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/11/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/11/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/11/OEPSSIMPLE.properties 
         #12
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/12/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/12/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/12/OEPSSIMPLE.properties 
         #15
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/15/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/15/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/15/OEPSSIMPLE.properties 
         #21
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/21/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/21/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/21/OEPSSIMPLE.properties 
         #22
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/22/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/22/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/22/OEPSSIMPLE.properties 
         #25
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/25/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/25/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/25/OEPSSIMPLE.properties 
         #51
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/51/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/51/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/51/OEPSSIMPLE.properties 
         #52
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/52/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/52/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/52/OEPSSIMPLE.properties 
         #55
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/55/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/55/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/8Queue_False/55/OEPSSIMPLE.properties 



         #16Queue
         #True
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/PCQ.properties 
         #11
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/11/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/11/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/11/OEPSSIMPLE.properties 
         #12
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/12/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/12/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/12/OEPSSIMPLE.properties 
         #15
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/15/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/15/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/15/OEPSSIMPLE.properties 
         #21
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/21/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/21/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/21/OEPSSIMPLE.properties 
         #22
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/22/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/22/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/22/OEPSSIMPLE.properties 
         #25
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/25/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/25/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/25/OEPSSIMPLE.properties 
         #51
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/51/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/51/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/51/OEPSSIMPLE.properties 
         #52
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/52/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/52/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/52/OEPSSIMPLE.properties 
         #55
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/55/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/55/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_True/55/OEPSSIMPLE.properties 

         #False
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/PCQ.properties 
         #11
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/11/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/11/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/11/OEPSSIMPLE.properties 
         #12
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/12/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/12/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/12/OEPSSIMPLE.properties 
         #15
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/15/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/15/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/15/OEPSSIMPLE.properties 
         #21
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/21/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/21/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/21/OEPSSIMPLE.properties 
         #22
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/22/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/22/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/22/OEPSSIMPLE.properties 
         #25
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/25/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/25/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/25/OEPSSIMPLE.properties 
         #51
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/51/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/51/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/51/OEPSSIMPLE.properties 
         #52
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/52/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/52/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/52/OEPSSIMPLE.properties 
         #55
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/55/EPSSIMPLE.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/55/EPSWFQ.properties 
java -jar -ea NetBench.jar projects/WFQ/run/Traffic_Pair/16Queue_False/55/OEPSSIMPLE.properties 

