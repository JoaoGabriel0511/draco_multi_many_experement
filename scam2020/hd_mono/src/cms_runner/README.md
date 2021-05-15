# CMS Runner

This is a CLI for running the HeuristicDesign library. The HeuristicDesign
library has been originally developed by Prof. Márcio Barros, and
it is available as a [supporting material](https://www.uniriotec.br/~marcio.barros/multiobjective/)
for the paper "Evaluating Modularization Quality as an Extra Objective in Multiobjective Software Module Clustering".
Note, here we are using a maven version of this library, available
in a [different repository](https://github.com/project-draco/cms)


### Limitations

   * The current version only supports the NSGAII algorithm
   
### Build and Install

   * Build and install the [maven library version](https://github.com/project-draco/cms) of the HeuristicDesign.
   * Build and install the [maven library version](https://github.com/project-draco/cms_mono_objective) of the HeuristicDesign MonoObjective version.
   * Execute the following command

```console
mvn clean compile assembly:single
```

The above command generates the cms_runner-<VERSION>-jar-with-dependencies.jar in the target
directory.


### Execution

   * Usage: run `java -jar ./target/cms_runner-1.0-SNAPSHOT-jar-with-dependencies.jar --help` to see the options. It should output something like: 
   
```
usage: CMSRunner [--algorithm <algorithm>] --input-dir <input-dir> |
       --input-file <input-file>  --output <output> --repetitions
       <repetitions>

Execute the HeuristicDesign tool

    --algorithm <algorithm>       The algorithm that should be used.
                                  Default NSGAII
    --input-dir <input-dir>       The path to a folder with MDG files
    --input-file <input-file>     Path to the MDG file
    --output <output>             Output file
    --repetitions <repetitions>   Number of repetitions

Please report issues at https://github.com/project-draco/cms_runner
```

   * Example:

```console
java -jar ./target/cms_runner-1.0-SNAPSHOT-jar-with-dependencies.jar
     --algorithm NSGAII
     --input-file /Users/rbonifacio/Documents/workspace-clusterizacao/cms_runner/samples/cohesion_100c_12p.txt
     --output out.txt
     --repetitions 20
```

