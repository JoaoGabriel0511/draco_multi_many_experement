package br.unirio.lns.hdesign.model;

import java.util.Vector;

/**
 * Classe que representa um projeto
 * 
 * @author Marcio Barros
 */
public class Project
{
	private String name;
	private Vector<ProjectPackage> packages;
	private Vector<ProjectClass> classes;

	/**
	 * Inicializa uma aplica��o
	 */
	public Project(String name)
	{
		this.name = name;
		this.packages = new Vector<ProjectPackage>();
		this.classes = new Vector<ProjectClass>();
	}
	
	/**
	 * Retorna o nome da aplica��o
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retorna o n�mero de pacotes da aplica��o
	 */
	public int getPackageCount()
	{
		return packages.size();
	}
	
	/**
	 * Retorna um pacote da aplica��o, dado seu �ndice
	 */
	public ProjectPackage getPackageIndex(int index)
	{
		return packages.elementAt(index);
	}

	/**
	 * Retorna o �ndice de um pacote no projeto
	 */
	public int getIndexForPackage(ProjectPackage _package)
	{
		return packages.indexOf(_package);
	}
	
	/**
	 * Retorna um pacote da aplica��o, dado seu nome
	 */
	public ProjectPackage getPackageName(String name)
	{
		for (ProjectPackage p : packages)
			if (p.getName().compareToIgnoreCase(name) == 0)
				return p;
		
		return null;
	}
	
	/**
	 * Adiciona um pacote na aplica��o
	 */
	public ProjectPackage addPackage(String name)
	{
		ProjectPackage aPackage = new ProjectPackage(name);
		packages.add(aPackage);
		return aPackage;
	}
	
	/**
	 * Retorna uma enumera��o dos pacotes da aplica��o
	 */
	public Iterable<ProjectPackage> getPackages()
	{
		return packages;
	}
	
	/**
	 * Retorna o n�mero de classes do projeto
	 */
	public int getClassCount()
	{
		return classes.size();
	}

	/**
	 * Retorna uma classe, dado seu �ndice no projeto
	 */
	public ProjectClass getClassIndex(int index)
	{
		return classes.elementAt(index);
	}

	/**
	 * Retorna uma classe, dado seu nome
	 */
	public ProjectClass getClassName(String name)
	{
		for (ProjectClass c : classes)
			if (c.getName().compareToIgnoreCase(name) == 0)
				return c;
		
		return null;
	}

	/**
	 * Retorna o �ndice de uma classe, dado seu nome
	 */
	public int getClassIndex(String name)
	{
		for (int i = 0; i < classes.size(); i++)
		{
			ProjectClass c = classes.elementAt(i);
			
			if (c.getName().compareToIgnoreCase(name) == 0)
				return i;
		}
		
		return -1;
	}

	/**
	 * Retorna o �ndice de uma classe no projeto
	 */
	public int getIndexForClass(ProjectClass _class)
	{
		return classes.indexOf(_class);
	}
	
	/**
	 * Adiciona uma classe no projeto
	 */
	public void addClass(ProjectClass c)
	{
		classes.add(c);
	}
	
	/**
	 * Remove uma classe do projeto, dado seu �ndice
	 */
	public void removeClass(int index)
	{
		classes.remove(index);
	}

	/**
	 * Adiciona uma depend�ncia entre duas classes no projeto
	 */
	public void addDependency(String sourceClass, String targetClass)
	{
		ProjectClass source = getClassName(sourceClass);
		
		if (source != null)
			source.addDependency(targetClass);
	}
	
	/**
	 * Retorna o n�mero de depend�ncias do projeto 
	 */
	public int getDependencyCount()
	{
		int count = 0;
		
		for(ProjectClass c: classes)
			count += c.getDependencyCount();
		
		return count;
	}
}