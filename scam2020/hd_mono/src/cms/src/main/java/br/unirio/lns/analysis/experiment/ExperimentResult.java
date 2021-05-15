package br.unirio.lns.analysis.experiment;

import java.util.Vector;

public class ExperimentResult
{
	private Vector<ExperimentInstance> instances;
	private String name;
	
	public ExperimentResult(String name)
	{
		this.name = name;
		this.instances = new Vector<ExperimentInstance>();
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public int getInstanceCount()
	{
		return instances.size();
	}
	
	public ExperimentInstance getInstanceIndex(int index)
	{
		return instances.elementAt(index);
	}
	
	public void addInstance(ExperimentInstance item)
	{
		instances.add(item);
	}
	
	public void removeInstance(int index)
	{
		instances.remove(index);
	}
}