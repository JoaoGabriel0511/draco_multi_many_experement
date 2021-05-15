package br.unirio.lns.conflict;

public class ConflictMeasurement
{
	private double sigma;
	
	private int occurrences;
	
	public ConflictMeasurement(int occurrences, double sigma)
	{
		this.sigma = sigma;
		this.occurrences = occurrences;
	}
	
	public double getSigma()
	{
		return sigma;
	}

	public int getOccurrences()
	{
		return occurrences;
	}
}