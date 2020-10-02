import java.util.*; import bunch.api.*;
public class BunchAPITest {

public BunchAPITest(String _INPUT_FILE_NAME)
{
BunchAPI api = new BunchAPI();
BunchProperties bp = new BunchProperties();

//set a lot of Bunch Properties
bp.setProperty(BunchProperties.MDG_INPUT_FILE_NAME, _INPUT_FILE_NAME);
bp.setProperty(BunchProperties.CLUSTERING_ALG, BunchProperties.ALG_HILL_CLIMBING);
bp.setProperty(BunchProperties.OUTPUT_FORMAT, BunchProperties.DOT_OUTPUT_FORMAT);
bp.setProperty(BunchProperties.OUTPUT_DIRECTORY, "bunch/dot/");
bp.setProperty(BunchProperties.MDG_PARSER_USE_SPACES, "False");

bp.setProperty(BunchProperties.MQ_CALCULATOR_CLASS, "bunch.TurboMQ");

api.setProperties(bp);

//System.out.println("Running...");
api.run();
//System.out.println("Done!");
Hashtable results = api.getResults();
//System.out.println("Results:");
String rt = (String)results.get(BunchAPI.RUNTIME);
String evals = (String)results.get(BunchAPI.MQEVALUATIONS);
//System.out.println("Runtime = " + rt + " ms.");
//System.out.println("Total MQ Evaluations = " + evals);

Hashtable [] resultLevels = (Hashtable[])results.get
(BunchAPI.RESULT_CLUSTER_OBJS);

//Output detailed information for each level
//for(int i = 0; i < resultLevels.length; i++)
//{
Hashtable lvlResults = resultLevels[0];
//System.out.println("***** LEVEL "+i+"*****");
String mq = (String)lvlResults.get(BunchAPI.MQVALUE);
String depth = (String)lvlResults.get(BunchAPI.CLUSTER_DEPTH);
String numC = (String)lvlResults.get(BunchAPI.NUMBER_CLUSTERS);
System.out.println("MQ Value = " + mq);
//System.out.println(" Best Cluster Depth = " + depth);
//System.out.println(" Number of Clusters in Best Partition = " +
//numC);
System.out.println();
//}

}
public static void main(String[] args) {
BunchAPITest bunchAPITest1 = new BunchAPITest(args[0]);
}
}
