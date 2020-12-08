package random.number.generator;

public class RandomWrapper {
	
	public static double rando()
	{
		return LEcuyer.rando();
 	}
        
        public static void restart(){
            LEcuyer.restart();
        }
	
	public static long unif(long low, long high){
		return LEcuyer.unif(low, high);
	}
        
        public static int[] createMixedArray(int minimum, int maximum){
            int[] mixed = new int[maximum-minimum+1];
            
            for(int i=minimum;i<=maximum;i++){//ordered Array
                mixed[i-minimum] = minimum+i;
            }
            
            //scramble
            for(int x=0;x<2;x++){
                for(int i=0; i<= maximum;i++){
                    int step = (int) unif(0, maximum);
                    int newIndex = (step+i) % (maximum+1);
                    int aux = mixed[i];
                    mixed[i] = mixed[newIndex];
                    mixed[newIndex] = aux;
                }
            }
            return mixed;
        }
        
        public static int createRandomNumber(int minValue,int maxValue, int gapValue){
            if((maxValue - minValue) % gapValue != 0) throw new RuntimeException("INTERVALO ESPECIFICADO NÃO É MÚLTIPLO DO GAP");
            int totalN = (maxValue - minValue) / gapValue;
            int rand = (int) RandomWrapper.unif(0, totalN);
            return minValue + (gapValue*rand);
        }
        
        public static float createRandomNumber(float minValue,float maxValue, float gapValue){
            if((maxValue - minValue) % gapValue > 0.001d) throw new RuntimeException("INTERVALO ESPECIFICADO NÃO É MÚLTIPLO DO GAP");
            int totalN = (int) ((maxValue - minValue) / gapValue);
            int rand = (int) RandomWrapper.unif(0, totalN);
            return minValue + (gapValue*rand);
        }
}
