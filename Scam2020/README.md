# SCM Performance Analyzer

- System requirements: python 3.7 +
- Steps:
    - Compile the SCM tools (Bunch and Draco) binarie's files (instructions below)
    - Run the analyzer with the desired parameters: 
        $ python3 main.py <test, small, medium, large, all>


## SCM Tools 

### Bunch

- System requirements: javac 1.8.0_265 +
- Compile java file and generates a `BunchAPITest.class`:
    $ javac -cp '.:bunch/Bunch-3.5.jar' bunch/BunchAPITest.java

### Draco

- System requirements: go 1.15.2 + 
- Draco requirements:
    $ go get -u github.com/project-draco/moea
- Compile go file and generates a binary file called `main`:
    $ go build -o draco/main draco/main.go

