package br.unirio.lns.analysis.reader;

public class ExperimentFileReaderException extends Exception
{
	private static final long serialVersionUID = -2117057147814141836L;

	public ExperimentFileReaderException(String message)
	{
		super(message);
	}

	public ExperimentFileReaderException(int line, String message)
	{
		this("Line " + line + ": " + message);
	}
}