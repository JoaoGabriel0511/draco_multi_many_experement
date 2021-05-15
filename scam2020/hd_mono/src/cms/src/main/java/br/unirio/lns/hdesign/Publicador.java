package br.unirio.lns.hdesign;

import java.util.Vector;

import br.unirio.lns.hdesign.model.Project;
import br.unirio.lns.hdesign.model.ProjectClass;

public class Publicador
{
	private static final int BARS = 50;
	
	@SuppressWarnings("unused")
	private int getMaximumDependenciesClass(Project project)
	{
		int maximum = 0;
		
		for (int i = project.getClassCount()-1; i >= 0; i--)
		{
			ProjectClass _class = project.getClassIndex(i);
			maximum = Math.max(_class.getDependencyCount(), maximum);
		}
		
		return maximum;
	}
	
	private void printHistogram(Project project)
	{
		int[] histogram = new int[BARS];
		
		for (int i = project.getClassCount()-1; i >= 0; i--)
		{
			ProjectClass _class = project.getClassIndex(i);
			int position = Math.min(_class.getDependencyCount(), BARS-1);
			histogram[position]++;
			//System.out.print(_class.getDependencyCount() + "\t");
		}
		
		for (int i = 0; i < BARS; i++)
			System.out.print(histogram[i] + "\t");

		System.out.println();
	}

	public void printProperties (Project project)
	{
		System.out.print(project.getName() + "\t");
		System.out.print(project.getClassCount() + "\t");
		System.out.print(project.getPackageCount() + "\t");
		System.out.print(project.getDependencyCount() + "\t");
		printHistogram(project);
	}

	public void printProperties (Vector<Project> projects)
	{
		System.out.println("Name\tClasses\tPackages\tDeps");

		for(Project p: projects)
			printProperties (p);
	}
}