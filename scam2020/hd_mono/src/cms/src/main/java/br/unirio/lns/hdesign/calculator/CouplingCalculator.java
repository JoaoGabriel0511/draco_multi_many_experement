package br.unirio.lns.hdesign.calculator;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;

import br.unirio.lns.hdesign.model.Project;
import br.unirio.lns.hdesign.model.ProjectClass;
import br.unirio.lns.hdesign.model.ProjectPackage;

/**
 * DEFINIC�ES:
 * 
 * - acoplamento = n�mero de depend�ncias que as classes de um pacote possuem com classes de fora
 *   do pacote. Deve ser minimizado.
 *   
 * - coes�o = n�mero de depend�ncias que as classes de um pacote possuem com outras classes do
 *   mesmo pacote. Deve ser maximizado (ou seja, minimizamos seu valor com sinal invertido)
 *   
 * - spread = partindo de zero e percorrendo cada pacote, acumula o quadrado da diferen�a entre
 *   o n�mero de classes do pacote e o n�mero de classes do menor pacote
 *   
 * - diferenca = diferen�a entre o n�mero m�ximo de classes em um pacote e o n�mero m�nimo de
 *   classes em um pacote
 * 
 * @author Marcio Barros
 */
public class CouplingCalculator
{
	private Project project;
	private int classCount;
	private int packageCount;
	private int[][] dependencies;
	private int[] originalPackage;
	private int[] newPackage;
	private int[] originalClasses;
	private int[] newClasses;

	/**
	 * Inicializa o calculador de acoplamento
	 */
	public CouplingCalculator(Project project) throws Exception
	{
		this.project = project;
		this.classCount = project.getClassCount();
		this.packageCount = project.getPackageCount();
		prepareClasses(project);
	}

	/**
	 * Prepara as classes para serem processadas pelo programa
	 */
	public void prepareClasses(Project project) throws Exception
	{
		dependencies = new int[classCount][classCount];
		
		originalPackage = new int[classCount];
		newPackage = new int[classCount];
		
		originalClasses = new int[packageCount];
		newClasses = new int[packageCount];

		for (int i = 0; i < classCount; i++)
		{
			ProjectClass _class = project.getClassIndex(i);
			int sourcePackageIndex = project.getIndexForPackage(_class.getPackage());
			
			originalPackage[i] = newPackage[i] = sourcePackageIndex;
			originalClasses[sourcePackageIndex]++;
			newClasses[sourcePackageIndex]++; 

			for (int j = 0; j < _class.getDependencyCount(); j++)
			{
				String targetName = _class.getDependencyIndex(j).getElementName();
				int classIndex = project.getClassIndex(targetName);
				
				if (classIndex == -1)
					throw new Exception ("Class not registered in project: " + targetName);
				
				dependencies[i][classIndex]++;
			}
		}
	}

	/**
	 * Inicializa o processo de c�lculo
	 */
	public void reset()
	{
		for (int i = 0; i < classCount; i++)
			newPackage[i] = originalPackage[i];

		for (int i = 0; i < packageCount; i++)
			newClasses[i] = originalClasses[i];
	}

	/**
	 * Move uma classe para um pacote
	 */
	public void moveClass(int classIndex, int packageIndex)
	{
		int actualPackage = newPackage[classIndex];
		
		if (actualPackage != packageIndex)
		{
			newClasses[actualPackage]--;
			newPackage[classIndex] = packageIndex;
			newClasses[packageIndex]++;
		}
	}

	/**
	 * Retorna o n�mero de classes de um pacote
	 */
	public int getClassCount(int packageIndex)
	{
		return newClasses[packageIndex];
	}

	/**
	 * Retorna a lista de classes de um pacote
	 */
	public String getClasses(int packageIndex)
	{
		String s = "";

		for (int i = 0; i < classCount; i++)
			if (newPackage[i] == packageIndex)
				s += project.getClassIndex(i).getName() + " ";

		return s;
	}

	/**
	 * Retorna o n�mero de movimentos de classes
	 */
	public int getMoveCount(int packageIndex)
	{
		int count = 0;
		
		for (int i = 0; i < classCount; i++)
			if (originalPackage[i] == packageIndex && newPackage[i] != packageIndex)
				count++;

		return count;
	}

	/**
	 * Retorna o n�mero de movimentos de classes
	 */
	public int getMoveCount()
	{
		int count = 0;

		for (int i = 0; i < classCount; i++)
			if (originalPackage[i] != newPackage[i])
				count++;

		return count;
	}

	/**
	 * Retorna o n�mero de movimentos de classes
	 */
	private int getMinimumClassCount()
	{
		int min = Integer.MAX_VALUE;

		for (int i = 0; i < packageCount; i++)
		{
			int count = newClasses[i];

			if (count < min)
				min = count;
		}

		return min;
	}

	/**
	 * Retorna o n�mero de movimentos de classes
	 */
	private int getMaximumClassCount()
	{
		int max = Integer.MIN_VALUE;

		for (int i = 0; i < packageCount; i++)
		{
			int count = newClasses[i];

			if (count > max)
				max = count;
		}

		return max;
	}

	/**
	 * Calcula o numero de depend�ncias com origem em um dado pacote e t�rmino em outro de um pacote
	 */
	private int countOutboundEdges(int packageIndex)
	{
		int edges = 0;

		for (int i = 0; i < classCount; i++)
		{
			int currentPackage = newPackage[i];
			
			if (currentPackage != packageIndex)
				continue;
			
			for (int j = 0; j < classCount; j++)
				if (dependencies[i][j] > 0 && newPackage[j] != currentPackage)
					edges++;
		}

		return edges;
	}

	/**
	 * Calcula o numero de depend�ncias com um pacote e t�rmino em um dado pacote
	 */
	private int countInboundEdges(int packageIndex)
	{
		int edges = 0;

		for (int i = 0; i < classCount; i++)
		{
			int currentPackage = newPackage[i];
			
			if (currentPackage == packageIndex)
				continue;
			
			for (int j = 0; j < classCount; j++)
				if (dependencies[i][j] > 0 && newPackage[j] == packageIndex)
					edges++;
		}

		return edges;
	}

	/**
	 * Calcula o numero de depend�ncias externas de um pacote
	 */
	private int countInterEdges(int packageIndex)
	{
		return countOutboundEdges(packageIndex);
	}
	
	/**
	 * Calcula o n�mero de depend�ncias internas de um pacote
	 */
	private int countIntraEdges(int packageIndex)
	{
		int cohesion = 0;

		for (int i = 0; i < classCount; i++)
		{
			int currentPackage = newPackage[i];
			
			if (currentPackage != packageIndex)
				continue;
			
			for (int j = 0; j < classCount; j++)
				if (dependencies[i][j] > 0 && newPackage[j] == currentPackage)
					cohesion++;
		}

		return cohesion;
	}

	/**
	 * Retorna a dispers�o da distribui��o de classes em pacotes
	 */
	public int calculateDifference()
	{
		int min = getMinimumClassCount();
		int max = getMaximumClassCount();
		return max - min;
	}

	/**
	 * Retorna a dispers�o da distribui��o de classes em pacotes
	 */
	public double calculateSpread()
	{
		int min = getMinimumClassCount();
		double spread = 0.0;

		for (int i = 0; i < packageCount; i++)
		{
			int count = newClasses[i];
			spread += Math.pow(count - min, 2);
		}

		return spread;
	}

	/**
	 * Calcula o acoplamento do projeto
	 */
	public int calculateCoupling()
	{
		int coupling = 0;

		for (int i = 0; i < classCount; i++)
		{
			int currentPackage = newPackage[i];
			
			for (int j = 0; j < classCount; j++)
				if (dependencies[i][j] > 0 && newPackage[j] != currentPackage)
					coupling += 2;
		}

		return coupling;
	}

	/**
	 * Calcula a coes�o do projeto
	 */
	public int calculateCohesion()
	{
		int cohesion = 0;

		for (int i = 0; i < classCount; i++)
		{
			int currentPackage = newPackage[i];
			
			for (int j = 0; j < classCount; j++)
				if (dependencies[i][j] > 0 && newPackage[j] == currentPackage)
					cohesion++;
		}

		return cohesion;
	}

	/**
	 * Calcula a coes�o do projeto
	 */
	public double calculateModularizationQuality()
	{
		double mq = 0.0;

		for (int i = 0; i < packageCount; i++)
		{
			int interEdges = countInboundEdges(i) + countOutboundEdges(i);
			int intraEdges = countIntraEdges(i);
			
			if (intraEdges != 0 && interEdges != 0)
			{
				double mf = intraEdges / (intraEdges + 0.5 * interEdges);
				mq += mf;
			}
		}

		return mq;
	}

	/**
	 * Apresenta o projeto no console
	 */
	public void printProject (Writer out) throws IOException
	{
		out.write(project.getName() + "; COUPLING = " + calculateCoupling() + "; COHESION = " + calculateCohesion() + "; SPREAD = " + calculateSpread() + "; MOVES = " + getMoveCount());
		out.write("\n");
		
		for (int i = 0; i < project.getPackageCount(); i++)
		{
			ProjectPackage _package = project.getPackageIndex(i); 
			int classes = getClassCount(i);
			int dependencies = countInterEdges(i);
			String sClasses = getClasses(i);
			out.write(_package.getName() + ": C = " + classes + "; D = " + dependencies + "; " + sClasses);
			out.write("\n");
		}
	}

	/**
	 * Apresenta o projeto no console
	 */
	public void printProjectCompact (Writer out) throws IOException
	{
		DecimalFormat df = new DecimalFormat("0.00");
		out.write(calculateCoupling() + "; " + calculateCohesion() + "; " + calculateDifference() + "; " + df.format(calculateModularizationQuality()) + "; " + getMoveCount() + "; ");
		
		for (int i = 0; i < project.getPackageCount(); i++)
			out.write("[" + getClasses(i) + "] ");

		out.write("\n");
	}
}