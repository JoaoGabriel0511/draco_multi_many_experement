package br.unirio.lns.analysis;

import java.util.Arrays;

public class Calculator
{
	// Mann-Whitney U for n1=n2=34 --> 5% u*=418, 1% u*=369	
	private int[][] MANN_WHITNEY_05P = new int[][]
	{
		{0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0},
		{0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	1,	1,	1,	1,	1,	2,	2,	2,	2},
		{0,	0,	0,	0,	0,	1,	1,	2,	2,	3,	3,	4,	4,	5,	5,	6,	6,	7,	7,	8},
		{0,	0,	0,	0,	1,	2,	3,	4,	4,	5,	6,	7,	8,	9,	10,	11,	11,	12,	13,	13},
		{0,	0,	0,	1,	2,	3,	5,	6,	7,	8,	9,	11,	12,	13,	14,	15,	17,	18,	19,	20},
		{0,	0,	1,	2,	3,	5,	6,	8,	10,	11,	13,	14,	16,	17,	19,	21,	22,	24,	25,	27},
		{0,	0,	1,	3,	5,	6,	8,	10,	12,	14,	16,	18,	20,	22,	24,	26,	28,	30,	32,	34},
		{0,	0,	2,	4,	6,	8,	10,	13,	15,	17,	19,	22,	24,	26,	29,	31,	34,	36,	38,	41},
		{0,	0,	2,	4,	7,	10,	12,	15,	17,	21,	23,	26,	28,	31,	34,	37,	39,	42,	45,	48},
		{0,	0,	3,	5,	8,	11,	14,	17,	20,	23,	26,	29,	33,	36,	39,	42,	45,	48,	52,	55},
		{0,	0,	3,	6,	9,	13,	16,	19,	23,	26,	30,	33,	37,	40,	44,	47,	51,	55,	58,	62},
		{0,	1,	4,	7,	11,	14,	18,	22,	26,	29,	33,	37,	41,	45,	49,	53,	57,	61,	65,	69},
		{0,	1,	4,	8,	12,	16,	20,	24,	28,	33,	37,	41,	45,	50,	54,	59,	63,	67,	72,	76},
		{0,	1,	5,	9,	13,	17,	22,	26,	31,	36,	40,	45,	50,	55,	59,	64,	67,	74,	78,	83},
		{0,	1,	5,	10,	14,	19,	24,	29,	34,	39,	44,	49,	54,	59,	64,	70,	75,	80,	85,	90},
		{0,	1,	6,	11,	15,	21,	26,	31,	37,	42,	47,	53,	59,	64,	70,	75,	81,	86,	92,	98},
		{0,	2,	6,	11,	17,	22,	28,	34,	39,	45,	51,	57,	63,	67,	75,	81,	87,	93,	99,	105},
		{0,	2,	7,	12,	18,	24,	30,	36,	42,	48,	55,	61,	67,	74,	80,	86,	93,	99,	106, 112},
		{0,	2,	7,	13,	19,	25,	32,	38,	45,	52,	58,	65,	72,	78,	85,	92,	99,	106, 113, 119},
		{0,	2,	8,	14,	20,	27,	34,	41,	48,	55,	62,	69,	76,	83,	90,	98,	105, 112, 119, 127}
	};
	
	public Calculator()
	{
	}
	
	public double calculateAverage(double[] values)
	{
		double sum = 0.0;
		
		for (int i = 0; i < values.length; i++)
			sum += values[i];
		
		return sum / values.length;
	}
	
	public double calculateStandardDeviation(double[] values)
	{
		double sum = 0.0;
		double avg = calculateAverage(values);
		
		for (int i = 0; i < values.length; i++)
			sum += (values[i] - avg) * (values[i] - avg);
		
		return Math.pow(sum / (values.length-1), 0.5);
	}
	
	private double calculateRank(double[] values, double value)
	{
		double rankSum = 0;
		int rankCount = 0;
		
		for (int i = 0; i < values.length  &&  values[i] <= value; i++)
		{
			if (values[i] == value)
			{
				rankSum += i;
				rankCount++;
			}
		}
		
		return (rankCount > 0) ? rankSum / rankCount : -1;
	}
	
	private void calculateRanks(double[] v1, double[] rank1, double[] v2, double[] rank2)
	{
		double[] values = new double[v1.length + v2.length];
		
		for (int i = 0; i < v1.length; i++)
			values[i] = v1[i];
		
		for (int i = 0; i < v2.length; i++)
			values[i + v1.length] = v2[i];
		
		Arrays.sort(values);
		
		for (int i = 0; i < v1.length; i++)
			rank1[i] = calculateRank(values, v1[i]) + 1;
		
		for (int i = 0; i < v2.length; i++)
			rank2[i] = calculateRank(values, v2[i]) + 1;
	}
	
	private double calculateSumRanks(double[] ranks)
	{
		double sum = 0.0;
		
		for (int i = 0; i < ranks.length; i++)
			sum += ranks[i];
		
		return sum;
	}
	
	@SuppressWarnings("unused")
	private double calculateAverageRank(double[] ranks)
	{
		return calculateSumRanks(ranks) / ranks.length;
	}
	
	private double normalDistribution (double x)
	{
		double P = 0.2316419, COEF1 = 0.31938153, COEF2 = -0.356563782, COEF3 = 1.781477937, COEF4 = -1.821255978, COEF5 = 1.330274429;
		double z = 1.0 / (1.0 + P * Math.abs(x));
		double serie = COEF1 * z + COEF2 * z * z + COEF3 * z * z * z + COEF4 * z * z * z * z + COEF5 * z * z * z * z * z;
		double normal = 1 - (1 / Math.pow(2 * Math.PI, 0.5)) * Math.exp(-x*x/2) * serie;
		return (x < 0) ? 1 - normal : normal;
	}

	@SuppressWarnings("unused")
	private double normalDistribution (double x, double mean, double stdev)
	{
		if (stdev == 0)
			return 0.0;
		
		return normalDistribution((x - mean) / stdev);
	}

	public boolean calculateMannWhitney(double[] v1, double v2[])
	{
		double[] rank1 = new double[v1.length];
		double[] rank2 = new double[v2.length];
		calculateRanks(v1, rank1, v2, rank2);
		
		int n1 = v1.length;
		int n2 = v2.length;
		
		//double mr1 = calculateAverageRank(rank1);
		//double mr2 = calculateAverageRank(rank2);
		
		double w1 = calculateSumRanks(rank1);
		double w2 = calculateSumRanks(rank2);

		if (n1 <= 20 && n2 <= 20)
		{
			double u1 = w1 - (n1 * (n1 + 1)) / 2.0;
			double u2 = w2 - (n2 * (n2 + 1)) / 2.0;
			double umin = (u1 > u2) ? u2 : u1;
			double ucrit = MANN_WHITNEY_05P[n1-1][n2-1];
			return umin >= ucrit;		// equal distributions
		}

		double wmin = (w1 > w2) ? w2 : w1;
		double mu = n1 * (n1 + n2 + 1) / 2;
		double su = Math.pow(n1 * n2 * (n1 + n2 + 1) / 12, 0.5);
		double z = (wmin - mu + 0.5) / su;
		double zProb = normalDistribution(z);
		return zProb > 0.05;			// equal distributions
	}
}