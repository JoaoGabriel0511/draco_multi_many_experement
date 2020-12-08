package br.com.ppgi.unirio.marlon.smc.experiment.output;

/**
 * Saida do experimeto em console
 * @author kiko
 */
public class SystemOutResultWriter extends ResultWriter{
		
	public SystemOutResultWriter(){
		out = System.out;
	}
	
	@Override
	public void close(){
		
	}
}
