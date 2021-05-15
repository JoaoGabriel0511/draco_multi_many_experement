package br.unirio.lns.analysis.experiment;

public class ParetoFrontierVertex
{
	private double[] values;
	
	public ParetoFrontierVertex(int length)
	{
		this.values = new double[length];
	}
	
	public double[] getValues()
	{
		return values;
	}
	
	public double getValue(int index)
	{
		return values[index];
	}
	
	public void set(double[] values)
	{
		for (int i = 0; i < this.values.length; i++)
			this.values[i] = values[i];
	}
	
	public void set(int index, double value)
	{
		this.values[index] = value;
	}
	
	public boolean sameVertex(ParetoFrontierVertex vertex)
	{
		for (int i = 0; i < values.length; i++)
			if (Math.abs(values[i] - vertex.values[i]) > 0.001)
				return false;

		return true;
	}

	public ParetoFrontierVertex clone()
	{
		ParetoFrontierVertex vertex = new ParetoFrontierVertex(values.length);
		vertex.set(values);
		return vertex;
	}
}