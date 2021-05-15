package br.unirio.lns.conflict;

import java.util.Vector;

import javax.management.modelmbean.XMLParseException;

import br.unirio.lns.hdesign.instancegen.FlatPublisherReaderException;
import br.unirio.lns.hdesign.model.Project;
import br.unirio.lns.hdesign.reader.CDAFlatReader;
import br.unirio.lns.hdesign.reader.CDAReader;

@SuppressWarnings("unused")
public class MainProgram
{
	private static String[] instanceFilenames50C = 
	{
		"data\\MQ Evaluation\\Cohesion 50C 7P.txt",
		"data\\MQ Evaluation\\Cohesion 50C 9P.txt",
		"data\\MQ Evaluation\\Cohesion 50C 10P.txt",
		"data\\MQ Evaluation\\Cohesion 50C 11P.txt",
		"data\\MQ Evaluation\\Cohesion 50C 13P.txt"
	};
	
	private static String[] instanceFilenames100C = 
	{
		"data\\MQ Evaluation\\Cohesion 100C 12P.txt",
		"data\\MQ Evaluation\\Cohesion 100C 14P.txt",
		"data\\MQ Evaluation\\Cohesion 100C 16P.txt",
		"data\\MQ Evaluation\\Cohesion 100C 18P.txt",
		"data\\MQ Evaluation\\Cohesion 100C 20P.txt"
	};
	
	private static String[] instanceFilenames150C = 
	{
		"data\\MQ Evaluation\\Cohesion 150C 18P.txt",
		"data\\MQ Evaluation\\Cohesion 150C 22P.txt",
		"data\\MQ Evaluation\\Cohesion 150C 25P.txt",
		"data\\MQ Evaluation\\Cohesion 150C 28P.txt",
		"data\\MQ Evaluation\\Cohesion 150C 31P.txt"
	};
	
	private static String[] instanceFilenames200C = 
	{
		"data\\MQ Evaluation\\Cohesion 200C 24P.txt",
		"data\\MQ Evaluation\\Cohesion 200C 28P.txt",
		"data\\MQ Evaluation\\Cohesion 200C 32P.txt",
		"data\\MQ Evaluation\\Cohesion 200C 36P.txt",
		"data\\MQ Evaluation\\Cohesion 200C 40P.txt"
	};
	
	private static String[] instanceFilenamesReals =
	{
		"data\\odem\\javacc.odem",
		"data\\odem\\junit-3.8.1 100C.odem",
		"data\\odem\\servletapi-2.3 74C.odem",
		"data\\odem\\xml-apis-1.0.b2 W3C-DOM.odem",
		"data\\odem\\jmetal.odem",
		"data\\odem\\xml-apis-1.0.b2.odem",
		"data\\odem\\dom4j-1.5.2 195C.odem",
		"data\\odem\\poormans_2.3 304C.odem",
		"data\\odem\\log4j-1.2.16 308C.odem",
		"data\\odem\\seemp.odem"
	};
	
	private static String[] instanceFilenamesDesigned =
	{
		"data\\designed\\Designed 8C 3P.txt"
	};
	
	public Project readFromXML (String filename) throws XMLParseException
	{
		CDAReader reader = new CDAReader();
		return reader.execute(filename);
	}

	public Project readFromFlat (String filename) throws FlatPublisherReaderException
	{
		CDAFlatReader reader = new CDAFlatReader(filename);
		reader.execute(filename);
		return reader.getProject();
	}
	
	private Vector<Project> readInstances(String[] filenames) throws FlatPublisherReaderException, XMLParseException
	{
		Vector<Project> instances = new Vector<Project>();
		
		for (String filename : filenames)
			if (filename.endsWith("txt"))
				instances.add (readFromFlat(filename));
			else
				instances.add (readFromXML(filename));
		
		return instances;
	}
	
	public static final void main(String[] args) throws Exception
	{
		MainProgram mp = new MainProgram();
		Vector<Project> instances = new Vector<Project>();
		instances.addAll(mp.readInstances(instanceFilenames50C));
		instances.addAll(mp.readInstances(instanceFilenames100C));
		instances.addAll(mp.readInstances(instanceFilenames150C));
		instances.addAll(mp.readInstances(instanceFilenames200C));
		instances.addAll(mp.readInstances(instanceFilenamesReals));
		//instances.addAll(mp.readInstances(instanceFilenamesDesigned));
		new ConflictCalculator().execute(instances);
	}
}