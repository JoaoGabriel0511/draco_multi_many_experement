package br.unirio.lns.hdesign.multiobjective;

import jmetal.base.Problem;
import jmetal.base.Solution;
import jmetal.base.Variable;
import jmetal.base.solutionType.IntSolutionType;
import jmetal.util.JMException;
import br.unirio.lns.hdesign.calculator.CouplingCalculator;
import br.unirio.lns.hdesign.model.Project;

/**
 * Classe que representa o problema de c�lculo de acoplamento em um projeto
 * 
 * @author Marcio Barros
 */
public class CouplingProblem extends Problem
{
	private static final long serialVersionUID = -8362599536848521L;
	private Project project;
	private CouplingCalculator calculator;

	/**
	 * Inicializa o problema de minimiza��o de acoplamento
	 */
	public CouplingProblem(Project project) throws Exception
	{
		this.project = project;
		this.calculator = new CouplingCalculator(project);

		numberOfVariables_ = project.getClassCount();
		numberOfObjectives_ = 4;
		numberOfConstraints_ = 0;

		setVariableLimits();
		solutionType_ = new IntSolutionType(this);
		variableType_ = new Class[numberOfVariables_];
		length_ = new int[numberOfVariables_];
		length_[0] = numberOfVariables_;
	}

	/**
	 * Define os limites das vari�veis que representa o problema
	 */
	private void setVariableLimits()
	{
		upperLimit_ = new double[numberOfVariables_];
		lowerLimit_ = new double[numberOfVariables_];

		for (int i = 0; i < numberOfVariables_; i++)
		{
			lowerLimit_[i] = 0;
			upperLimit_[i] = project.getPackageCount()-1;
		}
	}

	/**
	 * Retorna o calculador usado no problema
	 */
	public CouplingCalculator getCalculator()
	{
		return calculator;
	}

	/**
	 * Aplica uma solu��o sobre o calculador
	 */
	public void applySolutionToCalculator(Solution solution) throws JMException
	{
		Variable[] sequence = solution.getDecisionVariables();
		calculator.reset();

		for (int i = 0; i < sequence.length; i++)
		{
			int packageIndex = (int) sequence[i].getValue();
			calculator.moveClass(i, packageIndex);
		}
	}

	/**
	 * Calcula os objetivos com uma determinada solu��o
	 */
	@Override
	public void evaluate(Solution solution) throws JMException
	{
		applySolutionToCalculator(solution);
		
		double coupling = calculator.calculateCoupling();
		double cohesion = calculator.calculateCohesion();
		
		solution.setObjective(0, coupling);
		solution.setObjective(1, -cohesion);
		solution.setObjective(2, calculator.calculateDifference());
		solution.setObjective(3, -calculator.calculateModularizationQuality());
		//solution.setObjective(3, coupling + 10);
		//solution.setObjective(3, coupling - cohesion);
		//solution.setObjective(3, coupling * cohesion);
		//solution.setObjective(3, coupling / cohesion);
		//solution.setObjective(3, -cohesion / (cohesion + coupling / 2));
	}
}