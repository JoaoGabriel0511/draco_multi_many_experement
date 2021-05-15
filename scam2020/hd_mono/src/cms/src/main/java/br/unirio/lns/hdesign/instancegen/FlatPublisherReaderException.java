package br.unirio.lns.hdesign.instancegen;

/**
 * Classe que representa uma exce��o de carga de flat-file
 * 
 * @author Marcio Barros
 */
public class FlatPublisherReaderException extends Exception
{
	private static final long serialVersionUID = 8483375806576709630L;

	/**
	 * Inicializa uma exce��o de carga de flat-file, dada sua mensagem
	 */
	public FlatPublisherReaderException(String message)
	{
		super(message);
	}
}