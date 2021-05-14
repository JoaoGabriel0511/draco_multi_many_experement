# SCM Performance Analyzer

## How to run

### Docker

- System requirements:
    - Docker version 19.03.13
    - docker-compose version 1.25.0
- Steps to run (run for all mdgs):
    $ cd scam2020
    $ bash run.sh --start experiment_1

## SCM Tools 

### Bunch

- System requirements: javac 1.8.0_265 +
- Compile java file and generates a `BunchAPITest.class`:
    $ javac -cp '.:bunch/src/Bunch-3.5.jar' bunch/src/BunchAPITest.java

### Draco

- System requirements: go 1.15.2 + 
- Draco requirements:
    $ go get -u github.com/project-draco/moea
- Compile go file and generates a binary file called `main`:
    $ go build -o draco/main draco/main.go


## How to add new experiment
- Add a folder inside graphs directory and add all of the mdgs that are a part of the experiment/
- Example: graphs/example/test.mdg