
# SQWFQ evaluation

## Quick start

+ **single-switch-simulation** folder contains the java code of **experiments with Single-Switch Topology** in our paper.To run it, you can just run the `MainFromIntelliJ_SQEX2.java` file, located in `single-switch-simulation/src/main/java/ch.ethz.systems.netbench/core/run`.This file is responsible for executing all the simulations as configured in `single-switch-simulation/projects/WFQ/run/Flow_SQEX2`, and  generating the output results for those simulations in `single-switch-simulation/temp/WFQ/Flow_SQEX2`.

+ **leaf-spine-simulation** folder contains the java code of **experiments with Large-Scale Network** in our paper.To run it, you can just run the `MainFromIntelliJ_Flow_Leaf.java` file, located in `leaf-spine-simulation/src/main/java/ch.ethz.systems.netbench/core/run`.This file is responsible for executing all the simulations as configured in `leaf-spine-simulation/projects/WFQ/run/Flow_leaf/albert`, and  generating the output results for those simulations in `leaf-spine-simulation/temp/WFQ/Flow_leaf/albert/`.

---

## Getting started with [NetBench](https://github.com/ndal-eth/netbench)

#### 1. Software dependencies

* **Java 8:** Version 8 of Java; both Oracle JDK and OpenJDK are supported and produce under that same seed deterministic results. Additionally the project uses the Apache Maven software project management and comprehension tool (version 3+).

* **Python 3 (optional):** Recent version of Python 3 for analysis; be sure you can globally run `python3 <script.py>`.

#### 2. Building

1. Compile and run all tests in the project, make sure that they all pass; this can be done using the following maven command: `mvn compile test`

2. Build the executable `NetBench.jar` by using the following maven command: `mvn clean compile assembly:single`

#### 3. Running

1. Execute a demo run by using the following command: `java -jar -ea NetBench.jar ./example/runs/demo.properties`

2. After the run, the log files are saved in the `./temp/demo` folder

3. If you have python 2 installed, you can view calculated statistics about flow completion and port utilization (e.g. mean FCT, 99th %-tile port utilization, ....) in the `./temp/demo/analysis` folder.

## Software structure

There are three sub-packages in *netbench*: (a) core, containing core functionality, (b) ext (extension), which contains functionality implemented and quite thoroughly tested, and (c) xpt (experimental), which contains functionality not yet as thoroughly tested but reasonably vetted and assumed to be correct for the usecase it was written for.

The framework is written based on five core components:
1. **Network device**: abstraction of a node, can be a server (has a transport layer) or merely function as switch (no transport layer);
2. **Transport layer**: maintains the sockets for each of the flows that are started at the network device and for which it is the destination;
3. **Intermediary**: placed between the network device and transport layer, is able to modify each packet before arriving at the transport layer and after leaving the transport layer;
4. **Link**: quantifies the capabilities of the physical link, which the output port respects;
5. **Output port**: models output ports and their queueing behavior.

Look into `ch.ethz.systems.netbench.ext.demo` for an impression how to extend the framework.  If you've written an extension, it is necessary to add it in its respective selector in `ch.ethz.systems.netbench.run`. If you've added new properties, be sure to add them in the `ch.ethz.systems.netbench.config.BaseAllowedProperties` class.

More information about the framework can be found in the thesis located at [https://www.research-collection.ethz.ch/handle/20.500.11850/156350](https://www.research-collection.ethz.ch/handle/20.500.11850/156350) (section 4.2: NetBench: Discrete Packet Simulator).

---

## Reproducing the results in "TCP-friendly Packet Scheduling for Approximate Weighted Fair Queueing with a Single Queue"


### 1.Experiments with Single-Switch Topology

1. Make sure you understand and ran through the Getting Started section above.And this simulation generates data of figure 9,10,11,12,14 in our paper.

2. SQWFQ files are placed within the `single-switch-simulation/projects/WFQ/run/Flow_SQEX2` folder, which aims to be separated from the original [NetBench](https://github.com/ndal-eth/netbench) code for the sake of modularity.

* **Run configurations**:  All run configurations are placed in the `single-switch-simulation/projects/WFQ/run/Flow_SQEX2` folder.

 * **Output simulations**: The output of the runs are written to the `single-switch-simulation/temp/WFQ/Flow_SQEX2` folder. The folder contains the data of figure 9,10,11,12,14 in our paper.

 * **Main file to run the simulations**: The simulations can be executed by running the file `MainFromIntelliJ_SQEX2.java` file, located in `single-switch-simulation/src/main/java/ch.ethz.systems.netbench/core/run`. This file is responsible for (i) executing all the simulations as configured in `single-switch-simulation/projects/WFQ/run/Flow_SQEX2`,  (ii) generating the output results for those simulations in `single-switch-simulation/temp/WFQ/Flow_SQEX2`.

 ### 2.Experiments with Large-Scale Network

1. Make sure you understand and ran through the Getting Started section above.And this simulation generates data of figure 13 in our paper.

2. SQWFQ files are placed within the `leaf-spine-simulation/projects/WFQ/run/Flow_leaf/albert` folder, which aims to be separated from the original [NetBench](https://github.com/ndal-eth/netbench) code for the sake of modularity.

* **Run configurations**:  All run configurations are placed in the `leaf-spine-simulation/projects/WFQ/run/Flow_leaf/albert` folder.

 * **Output simulations**: The output of the runs are written to the `leaf-spine-simulation/temp/WFQ/Flow_leaf/albert/` folder. The folder contains the data of figure 13 in our paper.

 * **Main file to run the simulations**: The simulations can be executed by running the file `MainFromIntelliJ_Flow_Leaf.java` file, located in `leaf-spine-simulation/src/main/java/ch.ethz.systems.netbench/core/run`. This file is responsible for (i) executing all the simulations as configured in `leaf-spine-simulation/projects/WFQ/run/Flow_leaf/albert`,  (ii) generating the output results for those simulations in `leaf-spine-simulation/temp/WFQ/Flow_leaf/albert/`.

### 3.Run example

Let's now go for an example, wanting to reproduce the Experiments with Single-Switch Topology.

1. Look into folder `single-switch-simulation/projects/WFQ/run/Flow_SQEX2`. It contains the various `*.property` run configuration files that correspond to Figure 5a. 

2. Execute those simulations by pointing out their configuration files. They can be called individually or directly from a script or the `MainFromIntelliJ_SQEX2.java` file:
```javascript
    //EX1
    MainFromProperties.main(new String[]{"projects/WFQ/run/Flow_SQEX2/EX1/SQWFQ.properties"});
    MainFromProperties.main(new String[]{"projects/WFQ/run/Flow_SQEX2/EX1/PCQ.properties"});
    MainFromProperties.main(new String[]{"projects/WFQ/run/Flow_SQEX2/EX1/PCQ2.properties"});
    MainFromProperties.main(new String[]{"projects/WFQ/run/Flow_SQEX2/EX1/AIFO.properties"});
    MainFromProperties.main(new String[]{"projects/WFQ/run/Flow_SQEX2/EX1/PIFOOUR.properties"});
    MainFromProperties.main(new String[]{"projects/WFQ/run/Flow_SQEX2/EX1/DCTCP.properties"});

```

3. Look into folder `single-switch-simulation/temp/WFQ/Flow_SQEX2`, it contains the raw log files for each of the run simulations.

