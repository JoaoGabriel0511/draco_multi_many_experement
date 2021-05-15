package br.unirio.lns.hdesign.reader;

import java.util.Hashtable;
import java.util.Vector;

import br.unirio.lns.hdesign.instancegen.FlatPublisherReader;
import br.unirio.lns.hdesign.instancegen.FlatPublisherReaderException;
import br.unirio.lns.hdesign.model.DependencyType;
import br.unirio.lns.hdesign.model.ElementType;
import br.unirio.lns.hdesign.model.ElementVisibility;
import br.unirio.lns.hdesign.model.Project;
import br.unirio.lns.hdesign.model.ProjectClass;
import br.unirio.lns.hdesign.model.ProjectPackage;

/**
 * Leitor de inst�ncias vindas de arquivos do tipo flat-file
 * 
 * @author Marcio Barros
 */
public class CDAFlatReader extends FlatPublisherReader
{
	private Project project;
	private Vector<ProjectClass> classes;

	/**
	 * Inicializa o leitor de inst�ncias do problema vindas de flat files
	 */
	public CDAFlatReader(String projectName)
	{
		this.project = new Project(projectName);
		this.classes = new Vector<ProjectClass>();
	}
	
	/**
	 * Retorna o projeto lido do arquivo
	 */
	public Project getProject()
	{
		return project;
	}
	
	/**
	 * Retorna uma classe armazenada temporariamente em lista local, dado seu nome
	 */
	private ProjectClass getClassName(String name)
	{
		for (ProjectClass c : classes)
			if (c.getName().compareToIgnoreCase(name) == 0)
				return c;
		
		return null;
	}
	
	/**
	 * Registra uma entidade no projeto
	 */
	@Override
	protected void registerEntity(String type, String name, Hashtable<String, String> attributes) throws FlatPublisherReaderException
	{
		if (type.compareToIgnoreCase("class") == 0)
		{
			ProjectClass _class = new ProjectClass(name, ElementType.CLASS, ElementVisibility.PUBLIC, false);
			classes.add(_class);
			return;
		}
		
		if (type.compareToIgnoreCase("package") == 0)
		{
			project.addPackage(name);
			return;
		}
		
		generateException("unknown entity type '" + type + "'");
	}

	/**
	 * Registra um relacionamento vindo do arquivo
	 */
	@Override
	protected void registerRelationship(String type, String source, String target, Hashtable<String, String> attributes) throws FlatPublisherReaderException
	{
		if (type.compareToIgnoreCase("depends-to") == 0)
		{
			ProjectClass sourceClass = getClassName(source);
			
			if (sourceClass == null)
				generateException("unknown source class '" + source + "'");

			ProjectClass targetClass = getClassName(target);
			
			if (targetClass == null)
				generateException("unknown target class '" + target + "'");
			
			sourceClass.addDependency(target, DependencyType.USES);
			return;
		}
		
		if (type.compareToIgnoreCase("pertains-to") == 0)
		{
			ProjectClass sourceClass = getClassName(source);
			
			if (sourceClass == null)
				generateException("unknown source class '" + source + "'");

			ProjectPackage _package = project.getPackageName(target);
			
			if (_package == null)
				generateException("unknown target package '" + target + "'");
			
			sourceClass.setPackage(_package);
			project.addClass(sourceClass);
			classes.remove(sourceClass);
			return;
		}
		
		generateException("unknown entity type '" + type + "'");
	}

	/**
	 * Encerra a leitura do arquivo verificando se todas as classes foram transferidas para pacotes
	 */
	@Override
	public void cleanUp() throws FlatPublisherReaderException
	{
		if (classes.size() > 0)
			generateException(classes.size() + " classes are not referenced by packages");
	}
}