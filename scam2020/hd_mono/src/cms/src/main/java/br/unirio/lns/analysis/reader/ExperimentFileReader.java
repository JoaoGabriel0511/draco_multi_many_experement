package br.unirio.lns.analysis.reader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import br.unirio.lns.analysis.experiment.ExperimentCycle;
import br.unirio.lns.analysis.experiment.ExperimentInstance;
import br.unirio.lns.analysis.experiment.ExperimentResult;
import br.unirio.lns.analysis.experiment.ParetoFrontier;
import br.unirio.lns.analysis.experiment.ParetoFrontierVertex;

public class ExperimentFileReader
{
	private static double MAXIMUM_MQ = 1000000000.0;
	
	private int currentLineNumber;
	
	public ExperimentResult execute (String filename, int instanceCount, int cycleCount, int objectiveCount) throws ExperimentFileReaderException
	{
		try
		{
			return execute(extractFileName(filename), new FileInputStream(filename), instanceCount, cycleCount, objectiveCount);
		}
		catch(IOException e)
		{
			throw new ExperimentFileReaderException(e.getMessage());
		}
	}

	public ExperimentResult execute (String name, InputStream stream, int instanceCount, int cycleCount, int objectiveCount) throws ExperimentFileReaderException
	{
		currentLineNumber = 0;
		Scanner scanner = new Scanner(stream);
		
		try
		{
			ExperimentResult result = new ExperimentResult(name);
			
			for (int i = 0; i < instanceCount; i++)
				readInstance(result, i, cycleCount, objectiveCount, scanner);

			return result;
		}
		finally
		{
			scanner.close();
		}
	}
	
	private String extractFileName (String path)
	{
		int barPosition = path.lastIndexOf('\\');
		
		if (barPosition >= 0)
			path = path.substring(barPosition + 1);
		
		int pointPosition = path.lastIndexOf('.');
		
		if (pointPosition >= 0)
			path = path.substring(0, pointPosition);
		
		return path;
	}
	
	private String readLine (Scanner scanner)
	{
		String result = scanner.nextLine();
		currentLineNumber++;
		return result;
	}
	
	private int parseInteger (String s) throws ExperimentFileReaderException
	{
		try
		{
			return Integer.parseInt(s);
		}
		catch(Exception e)
		{
			throwException ("invalid integer value");
		}
		
		return 0;
	}
	
	private double parseDouble (String s) throws ExperimentFileReaderException
	{
		try
		{
			s = s.replace(',', '.');
			return Double.parseDouble(s);
		}
		catch(Exception e)
		{
			throwException ("invalid double value");
		}
		
		return 0;
	}
	
	private void throwException (String message) throws ExperimentFileReaderException
	{
		throw new ExperimentFileReaderException(currentLineNumber, message);
	}

	private void readInstance (ExperimentResult result, int instanceCount, int cycleCount, int objectiveCount, Scanner scanner) throws ExperimentFileReaderException
	{
		ExperimentInstance instance = new ExperimentInstance(objectiveCount);
		readInstanceHeader (instance, instanceCount, scanner);
		
		for (int i = 0; i < cycleCount; i++)
			readCycle (instance, i, objectiveCount, scanner);

		readBestFrontierHeader(instance, objectiveCount, scanner);
		result.addInstance(instance);
	}
	
	private void readBestFrontierHeader(ExperimentInstance instance, int objectiveCount, Scanner scanner) throws ExperimentFileReaderException
	{
		String firstHeaderLine = readLine(scanner);
		
		boolean mustCalculate = (firstHeaderLine.compareTo("Calculate Best Frontier:") == 0);
		boolean isPresented = firstHeaderLine.compareTo("Final Pareto Frontier:") == 0;

		if (!mustCalculate && !isPresented)
			throwException("expected final Pareto Frontier header or calculation header");

		ParetoFrontier calculatedBest = instance.calculateBestFrontier();

		if (isPresented)
		{
			readParetoFrontier(instance.getFrontier(), objectiveCount, scanner);
		}
		else
		{
			instance.getFrontier().merge(calculatedBest);

			if (scanner.hasNextLine())
				readLine(scanner);
		}
		
		if (!calculatedBest.isEqual(instance.getFrontier()))
			throwException("the calculated best frontier differs from the estimated one");
	}

	private void readInstanceHeader(ExperimentInstance instance, int instanceCount, Scanner scanner) throws ExperimentFileReaderException
	{
		String firstHeaderLine = readLine(scanner);
		
		if (firstHeaderLine.compareTo("=============================================================") != 0)
			throwException("expected header start");

		String secondHeaderLine = readLine(scanner);
		
		String headerStart = "Instance #" + instanceCount;
		
		if (!secondHeaderLine.startsWith(headerStart))
			throwException("expected header instance count");
		
		if (secondHeaderLine.length() > headerStart.length())
			instance.setName(secondHeaderLine.substring(headerStart.length()).trim());
		else
			instance.setName("#" + instanceCount);

		String thirdHeaderLine = readLine(scanner);
		
		if (thirdHeaderLine.compareTo("=============================================================") != 0)
			throwException("expected header finish");

		String fourthHeaderLine = readLine(scanner);
		
		if (fourthHeaderLine.length() != 0)
			throwException("expected blank line after header finish");
	}

	private void readCycle (ExperimentInstance instance, int cycleCount, int objectiveCount, Scanner scanner) throws ExperimentFileReaderException
	{
		ExperimentCycle cycle = instance.createCycle();
		readCycleHeader (cycle, cycleCount, scanner);
		readParetoFrontier(cycle.getFrontier(), objectiveCount, scanner);
		instance.addCycle(cycle);
	}

	private void readCycleHeader(ExperimentCycle cycle, int cycleCount, Scanner scanner) throws ExperimentFileReaderException
	{
		String line = readLine(scanner);
		String cycleHeader = "Cycle #" + cycleCount;
		
		if (!line.startsWith(cycleHeader))
			throwException("expected 'Cycle #" + cycleCount + "'");
		
		line = line.substring(cycleHeader.length());
		
		if (!line.startsWith(" ("))
			throwException("expected opening parenthesis after cycle header");

		line = line.substring(2);

		int position = line.indexOf(" ms");
		
		if (position <= 0)
			throwException("expected execution time in miliseconds");
		
		int executionTime = parseInteger(line.substring(0, position));
		
		line = line.substring(position + 3);
		
		if (!line.startsWith("; "))
			throwException("expected semicolon after execution time");

		line = line.substring(2);

		position = line.indexOf(" best solutions");
		
		if (position <= 0)
			throwException("expected number of best solutions");
		
		//int bestSolutions = parseInteger(line.substring(0, position));
		parseInteger(line.substring(0, position));
		
		cycle.setExecutionTime(executionTime);
		//cycle.setBestSolutions(bestSolutions);
	}

	private void readParetoFrontier(ParetoFrontier frontier, int objectiveCount, Scanner scanner) throws ExperimentFileReaderException
	{
		String s = readLine(scanner);
		
		while (s.length() > 0)
		{
			int position = s.indexOf(";");
			
			if (position <= 0)
				throwException("expected coupling entry in Pareto front");
			
			int coupling = parseInteger(s.substring(0, position));
			s = s.substring(position+2);

			
			position = s.indexOf(";");
			
			if (position <= 0)
				throwException("expected cohesion entry in Pareto front");
			
			int cohesion = parseInteger(s.substring(0, position));
			s = s.substring(position+2);

			
			position = s.indexOf(";");
			
			if (position <= 0)
				position = s.length();
			
			int difference = parseInteger(s.substring(0, position));

			double mq = MAXIMUM_MQ;
			
			if (position < s.length())
			{
				s = s.substring(position+2);
				mq = parseDouble(s);
				if (mq > 0) mq = -mq;
			}

			ParetoFrontierVertex vertex = new ParetoFrontierVertex(frontier.getObjectiveCount());
			vertex.set(0, coupling);
			vertex.set(1, cohesion);
			if (objectiveCount > 2) vertex.set(2, difference);
			if (objectiveCount > 3) vertex.set(3, mq);
			frontier.addVertex(vertex);
			
			s = (scanner.hasNextLine()) ? readLine(scanner) : "";
		}
	}
}