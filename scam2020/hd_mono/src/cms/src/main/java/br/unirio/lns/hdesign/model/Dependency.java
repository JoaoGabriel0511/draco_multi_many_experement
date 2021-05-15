package br.unirio.lns.hdesign.model;

/**
 * Classe que representa uma depend�ncia entre duas classes
 * 
 * @author Marcio Barros
 */
public class Dependency
{
	private String elementName;
	private DependencyType type;

	/**
	 * Inicializa uma depend�ncia de uma classe para outra
	 */
	public Dependency(String name, DependencyType type)
	{
		this.elementName = name;
		this.type = type;
	}
	
	/**
	 * Retorna o nome da classe dependida
	 */
	public String getElementName()
	{
		return elementName;
	}
	
	/**
	 * Retorna o tipo da depend�ncia
	 */
	public DependencyType getType()
	{
		return type;
	}
}