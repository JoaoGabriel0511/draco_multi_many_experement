package br.unb.cic.cms.runner.algorithm;

import br.com.ppgi.unirio.marlon.smc.experiment.output.ResultWriter;
import br.com.ppgi.unirio.marlon.smc.instance.file.InstanceFileWorker;
import br.com.ppgi.unirio.marlon.smc.instance.file.bunch.BunchInstanceFileWorker;
import br.com.ppgi.unirio.marlon.smc.mdg.ClusterMetrics;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import br.com.ppgi.unirio.marlon.smc.mdg.simplifier.MDGSimplifier;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive.InitialSolutionFactory;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.LNSConfiguration;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.LNSConfigurationBuilderFixedRandom;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.LNSConfigurationBuilderRandom;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.LargeNeighborhoodSearch;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.builder.DestrutiveMethodFactory;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.builder.RepairMethodFactory;
import random.number.generator.RandomWrapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LNS {

    private static final boolean simplify = true;

    private static final int RANDONS_CONFIGS_TO_TEST = 1;//2000//1000;//10000
    private final String PREFIX_NAME;

    private final Map<LNSConfigurationBuilderRandom.FILTER_NAMES,Object[]> FIXED_VALUES;
    private final LNSConfigurationBuilderRandom.FILTER_NAMES COMPARE_PARAM;
    private final Object[] COMPARE_PARAM_VALUES;

    {
        FIXED_VALUES = new HashMap<>();
        FIXED_VALUES.put(LNSConfigurationBuilderRandom.FILTER_NAMES.USE_SIMMULATED_ANNEALING, new Object[]{false});
        FIXED_VALUES.put(LNSConfigurationBuilderRandom.FILTER_NAMES.TIME_LIMIT, new Object[]{-1});
        FIXED_VALUES.put(LNSConfigurationBuilderRandom.FILTER_NAMES.ITERATION_LIMIT,new Object[]{500});
        FIXED_VALUES.put(LNSConfigurationBuilderRandom.FILTER_NAMES.NO_IMPROVEMENT_LIMIT,new Object[]{-1});

        FIXED_VALUES.put(LNSConfigurationBuilderRandom.FILTER_NAMES.INITIAL_SOLUTION_METHOD, new Object[]{InitialSolutionFactory.CREATION_METHOD.CAMQ});//experimento 01
        FIXED_VALUES.put(LNSConfigurationBuilderRandom.FILTER_NAMES.REPAIR_METHOD, new Object[]{RepairMethodFactory.REPAIR_METHOD.RGBI, RepairMethodFactory.REPAIR_METHOD.RGBIR});//experimento 02
        FIXED_VALUES.put(LNSConfigurationBuilderRandom.FILTER_NAMES.DESTRUTIVE_METHOD, new Object[]{DestrutiveMethodFactory.DESTRUTIVE_METHOD.DR});//experimento 03

        FIXED_VALUES.remove(LNSConfigurationBuilderRandom.FILTER_NAMES.ITERATION_LIMIT);
        FIXED_VALUES.put(LNSConfigurationBuilderRandom.FILTER_NAMES.ITERATION_LIMIT,new Object[]{-1});//sem limite de iteração
        FIXED_VALUES.put(LNSConfigurationBuilderRandom.FILTER_NAMES.ALGORITHM_NO_IMPROVEMENT_LIMIT,new Object[]{1000});
        FIXED_VALUES.put(LNSConfigurationBuilderRandom.FILTER_NAMES.DESTRUCTION_FACTOR,new Object[]{LNSConfigurationBuilderFixedRandom.DESTRUCTION_FACTOR_VALUES[1]});
        FIXED_VALUES.put(LNSConfigurationBuilderRandom.FILTER_NAMES.DESTRUCTION_FACTOR_PROBABILITY, new Object[]{1});
        //PREFIX_NAME = "ALL_FIXED_13_MIXED_1000_NO_LIMIT";
        //PREFIX_NAME = "ALL_FIXED_18_MIXED_1000_NO_LIMIT";
        PREFIX_NAME = "ALL_FIXED_18_MIXED_1000_NO_LIMIT_DETAILS";

        COMPARE_PARAM=LNSConfigurationBuilderRandom.FILTER_NAMES.NONE;
        COMPARE_PARAM_VALUES = new Object[] {""};
    }


    public void execute(List<File> instances, int repetitions) throws Exception {
        InstanceFileWorker<ModuleDependencyGraph> worker = new BunchInstanceFileWorker();

        System.out.println("INSTANCE;MQ;TEMPO");

        for(int i = 0; i < instances.size(); i++) {
            ModuleDependencyGraph mdg = worker.readInstanceFile(instances.get(i));
            runAlgorithm(mdg, repetitions);
            afterInstanceExecution();
        }
    }

    private void runAlgorithm(ModuleDependencyGraph mdg, int repetitions) {
        LNSConfigurationBuilderRandom configurationBuilder = new LNSConfigurationBuilderFixedRandom();
        String fixedParam = COMPARE_PARAM.toString();
        try {
            String simplifiedS = "";
            if(simplify){
                simplifiedS = "_SIMPLIFIED";
            }
            String outPath = PREFIX_NAME +"_" + fixedParam+simplifiedS+"/"+"data";
            ResultWriter out = ResultWriter.configureResultWriter(ResultWriter.OUTPUT.FILE,outPath, mdg.getName());

            System.out.println(mdg.getName());
            if(simplify){
                MDGSimplifier mDGSimplifier = MDGSimplifier.simplify(mdg);
                mdg = mDGSimplifier.getMdg();
            }
            for (int configN = 0; configN < RANDONS_CONFIGS_TO_TEST; configN++){
                LNSConfiguration config = configurationBuilder.buildRandomConfiguration(mdg, FIXED_VALUES);
                System.out.print("CONFIG: " + configN);
                for (Object currentValue : COMPARE_PARAM_VALUES) {//executar para cada configuração especifica
                    configurationBuilder.changeParameterValue(config, COMPARE_PARAM, currentValue);//acertar a config com o parametro atual
                    System.out.print(" PARAM: "+ currentValue.toString());
                    for(int execution = 0; execution < repetitions;execution++){
                        LargeNeighborhoodSearch lns = new LargeNeighborhoodSearch(config);
                        lns.execute();
                        saveSearchStatus(out, mdg, lns, configN, execution, currentValue);
                    }
                }
                System.out.println("");
            }
            out.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            throw new RuntimeException(ex);
        }

    }

    private void afterInstanceExecution() {
        RandomWrapper.restart();
    }


    private String testName() {
        return "LNS Execution";
    }

    private void saveSearchStatus(ResultWriter out, ModuleDependencyGraph mdg, LargeNeighborhoodSearch lns, int configN, int executionN, Object currentValue){
        ClusterMetrics cm = lns.getBestSolutionFound();
        out.writeLine("MQ: " + cm.calculateMQ(), ";Number of Clusters: " + cm.getTotalClusteres());
        System.out.println("MQ: " + cm.calculateMQ() + ";Number of Clusters: " + cm.getTotalClusteres());
    }
}
