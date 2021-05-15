package br.unb.cic.cms.runner;

import br.unb.cic.cms.runner.algorithm.Algorithm;
import br.unb.cic.cms.runner.algorithm.ClusteringProblemBuilder;
import br.unb.cic.cms.runner.algorithm.LNS;
import br.unirio.lns.hdesign.model.Project;
import br.unirio.lns.hdesign.multiobjective.CouplingProblem;
import br.unirio.lns.hdesign.multiobjective.Experiment;
import br.unirio.lns.hdesign.reader.CDAFlatReader;

import org.apache.commons.cli.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class MainProgram {

    public static final String ALGORITHM = "algorithm";
    public static final String INPUT_FILE = "input-file";
    public static final String INPUT_DIR = "input-dir";
    public static final String OUTPUT = "output";
    public static final String REPETITIONS = "repetitions";
    private Options options;

    public static void main(String args[]) {
        MainProgram main = new MainProgram();
        try {
            main.initOptions();
            main.processOptions(args);
        }catch(org.apache.commons.cli.ParseException e) {
            main.usageInfo();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void usageInfo() {
        HelpFormatter formatter = new HelpFormatter();
        String header = "\nExecute the HeuristicDesign tool\n\n";
        String footer = "\nPlease report issues at https://github.com/project-draco/cms_runner";

        formatter.printHelp("CMSRunner", header, options, footer, true);
    }

    /*
     * Execute the experiment according to the
     * command line args.
     */
    private void processOptions(String args[]) throws Exception {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);


        if(cmd.hasOption(ALGORITHM) && cmd.getOptionValue(ALGORITHM).equals(Algorithm.LNS.name())) {
            monoObjectiveExecution(cmd);
        }
        else {
            multiObjectiveExecution(cmd);
        }
    }

    private void monoObjectiveExecution(CommandLine cmd) throws Exception {
        List<File> instances = listMDGFiles(cmd).stream().map(f -> new File(f)).collect(Collectors.toList());
        LNS algorithm = new LNS();

        int repetitions = cmd.hasOption("repetitions")
                ? Integer.parseInt(cmd.getOptionValue("repetitions"))
                : 1;

        algorithm.execute(instances, repetitions);
    }

    private void multiObjectiveExecution(CommandLine cmd) throws Exception {
        Vector<Project> instances = loadProjects(listMDGFiles(cmd));

        int repetitions = cmd.hasOption("repetitions")
                ? Integer.parseInt(cmd.getOptionValue("repetitions"))
                : 1;

        ClusteringProblemBuilder builder = new ClusteringProblemBuilder(cmd.getOptionValue(ALGORITHM));
        Experiment<CouplingProblem, Project> experiment = new Experiment<>();
        experiment.runCycles(cmd.getOptionValue(OUTPUT), builder, instances, repetitions);
    }

    /*
     * Load CMS projects from a list of files.
     */
    private Vector<Project> loadProjects(List<String> files) throws Exception {
        Vector<Project> projects = new Vector<>();
        for(String file : files) {
            CDAFlatReader reader = new CDAFlatReader(file);
            reader.execute(file);
            projects.add(reader.getProject());
        }
        return projects;
    }

    /*
     * List the selected MDG files, from the
     * command line arguments.
     */
    private Vector<String> listMDGFiles(CommandLine cmd) throws Exception {
        String path = cmd.hasOption(INPUT_DIR) ? cmd.getOptionValue(INPUT_DIR) : cmd.getOptionValue(INPUT_FILE);
        File file = new File(path);

        List<String> mdgFiles = new ArrayList<>();

        if(file.exists() && file.isDirectory()) {
            mdgFiles = Arrays.stream(file.list())
                        .filter(f -> f.endsWith("mdg"))
                        .collect(Collectors.toList());
        }
        else if(file.exists()) {
            mdgFiles.add(file.getAbsolutePath());
        }
        else {
            throw new Exception("The path " + path + " does not exist");
        }
        return new Vector<>(mdgFiles);
    }

    public void initOptions() {
        options = new Options();

        options.addOption(Option.builder()
                .longOpt(ALGORITHM)
                .argName(ALGORITHM)
                .desc("The algorithm that should be used. " +
                        "The valid options are: (" + Algorithm.validOptions() +
                        "). Default NSGAII.")
                .argName("algorithm")
                .hasArg()
                .build());

        Option inputFile = Option.builder()
                .longOpt(INPUT_FILE)
                .argName(INPUT_FILE)
                .hasArg()
                .desc("Path to the MDG file")
                .build();

        Option inputDir = Option.builder()
                .longOpt(INPUT_DIR)
                .argName(INPUT_DIR)
                .hasArg()
                .desc("The path to a folder with MDG files")
                .build();

        OptionGroup inputGroup = new OptionGroup();

        inputGroup.setRequired(true);
        inputGroup.addOption(inputFile);
        inputGroup.addOption(inputDir);

        options.addOptionGroup(inputGroup);

        options.addOption(Option.builder()
                .longOpt(OUTPUT)
                .argName(OUTPUT)
                .hasArg()
                .required()
                .desc("Output file")
                .build());

        options.addOption(Option.builder()
                .longOpt(REPETITIONS)
                .argName(REPETITIONS)
                .required()
                .hasArg()
                .desc("Number of repetitions")
                .build());
    }
}
