package br.com.ppgi.unirio.marlon.smc.instance.file;

public class InstanceParseException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1399166319564097433L;

	public InstanceParseException(Exception e){
		super(e);
	}
	
	public InstanceParseException(String s){
		super(s);
	}
}
