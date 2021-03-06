package sobol.problems.clustering.generic.model;

/**
 * Classe que representa uma dependĂȘncia entre duas classes
 * 
 * @author Marcio Barros
 */
public class Dependency
{
	private String elementName;
	private DependencyType type;

	/**
	 * Inicializa uma dependĂȘncia de uma classe para outra
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
	 * Retorna o tipo da dependĂȘncia
	 */
	public DependencyType getType()
	{
		return type;
	}
}