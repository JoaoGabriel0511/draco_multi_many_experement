package br.unirio.lns.analysis.experiment;

import java.util.Vector;

public class ParetoFrontier
{
	private int objectiveCount;
	private Vector<ParetoFrontierVertex> vertices;
	
	public ParetoFrontier(int objectiveCount)
	{
		this.objectiveCount = objectiveCount;
		this.vertices = new Vector<ParetoFrontierVertex>();
	}
	
	public int getObjectiveCount()
	{
		return objectiveCount;
	}
	
	public int getVertexCount()
	{
		return vertices.size();
	}
	
	private ParetoFrontierVertex getVertexIndex(int index)
	{
		return vertices.elementAt(index);
	}
	
	public void setVertex (int index, double[] values)
	{
		ParetoFrontierVertex vertex = getVertexIndex(index); 
		
		for (int i = 0; i < objectiveCount; i++)
			vertex.set(i, values[i]);
	}
	
	public void setVertex (int index, int cellIndex, double value)
	{
		ParetoFrontierVertex vertex = getVertexIndex(index); 
		vertex.set(cellIndex, value);
	}
	
	private double[] getVertex(int index)
	{
		ParetoFrontierVertex vertex = getVertexIndex(index); 
		return vertex.getValues();
	}
	
	public double getVertex(int index, int cellIndex)
	{
		return getVertex(index)[cellIndex];
	}
	
	private DominationStatus dominates(ParetoFrontierVertex currentVertex, ParetoFrontierVertex newVertex)
	{
		boolean currentAlwaysDominated = true;
		boolean newAlwaysDominated = true;
		
		for (int i = 0; i < objectiveCount; i++)
		{
			double currentObjective = currentVertex.getValue(i);
			double newObjective = newVertex.getValue(i); 
		
			if (currentObjective < newObjective)
				newAlwaysDominated = false;

			if (currentObjective > newObjective)
				currentAlwaysDominated = false;
				
			if (!currentAlwaysDominated && !newAlwaysDominated)
				return DominationStatus.NON_DOMINATING;
		}
				
		if (currentAlwaysDominated)
			return DominationStatus.DOMINATED;
				
		return DominationStatus.DOMINATOR;
	}
	
	public void addVertex(ParetoFrontierVertex vertex)
	{
		DominationStatus newStatus = DominationStatus.NON_DOMINATING;
		
		for (int j = vertices.size()-1; j >= 0; j--)
		{
			ParetoFrontierVertex current = vertices.get(j);
			DominationStatus status = dominates(current, vertex);
			
			if (status == DominationStatus.DOMINATOR)
				vertices.remove(j);
			
			if (status == DominationStatus.DOMINATED)
				newStatus = DominationStatus.DOMINATED;
		}
			
		if (newStatus != DominationStatus.DOMINATED)
			vertices.add(vertex);
	}
	
	public void removeVertex(int index)
	{
		vertices.remove(index);
	}
	
	public void merge(ParetoFrontier second)
	{
		for (int i = 0; i < second.getVertexCount(); i++)
			addVertex(second.getVertexIndex(i));

		/*		
		Vector<ParetoFrontierVertex> newVertices = new Vector<ParetoFrontierVertex>();

		// Collects non-dominated vertices from the second frontier
		for (int i = 0; i < second.getVertexCount(); i++)
		{
			ParetoFrontierVertex v = second.getVertexIndex(i);
			
			if (!isDominated(this, v))
				newVertices.add(v.clone());
		}

		// Count of eliminated vertices
		int countRemoved = 0;
		
		// Eliminates vertices from the original frontier that are dominated by the new ones 
		for (int i = 0; i < newVertices.size(); i++)
		{
			ParetoFrontierVertex newVertex = newVertices.elementAt(i);
			
			for (int j = getVertexCount()-1; j >= 0; j--)
			{
				ParetoFrontierVertex v = getVertexIndex(j);
				
				if (newVertex.dominates(v) == DominationStatus.DOMINATED)
				{
					removeVertex(j);
					countRemoved++;
				}
			}
		}

		// Add the new vertices
		for (int i = 0; i < newVertices.size(); i++)
		{
			ParetoFrontierVertex newVertex = newVertices.elementAt(i);
			vertices.add(newVertex);
		}
		
		return newVertices.size() + " added, " + countRemoved + " removed";*/
	}

	public ParetoFrontier clone()
	{
		ParetoFrontier _copy = new ParetoFrontier(objectiveCount);
		
		for (int i = 0; i < getVertexCount(); i++)
		{
			ParetoFrontierVertex v = getVertexIndex(i);
			ParetoFrontierVertex _vcopy = new ParetoFrontierVertex(objectiveCount);
			_vcopy.set(v.getValues());
			_copy.vertices.add(_vcopy);
		}
		
		return _copy;
	}
	
	public boolean isEqual(ParetoFrontier second)
	{
		if (getObjectiveCount() != second.getObjectiveCount())
			return false;
		
		for (int i = 0; i < second.getVertexCount(); i++)
			if (!this.hasVertex(second.getVertexIndex(i)))
				return false;
		
		for (int i = 0; i < this.getVertexCount(); i++)
			if (!second.hasVertex(this.getVertexIndex(i)))
				return false;
		
		return true;
	}

	public int countCommons(ParetoFrontier f2)
	{
		int count = 0;
		
		for (int i = 0; i < f2.getVertexCount(); i++)
		{
			ParetoFrontierVertex vertex = f2.getVertexIndex(i);
			
			if (hasVertex(vertex))
				count++;
		}

		return count;
	}

	private boolean hasVertex(ParetoFrontierVertex vertex)
	{
		for (int i = 0; i < getVertexCount(); i++)
		{
			ParetoFrontierVertex v = getVertexIndex(i);
			
			if (v.sameVertex(vertex))
				return true;
		}
		
		return false;
	}
	
	public double[][] getValues()
	{
		double[][] vertex = new double[getVertexCount()][];
		
		for (int i = 0; i < getVertexCount(); i++)
		{
			double[] v = getVertexIndex(i).getValues();
			vertex[i] = new double[v.length];
			
			for (int j = 0; j < v.length; j++)
				vertex[i][j] = v[j];
		}
		
		return vertex;
	}
}

enum DominationStatus
{
	NON_DOMINATING,
	DOMINATED,
	DOMINATOR;
}