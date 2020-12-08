package br.com.ppgi.unirio.marlon.smc.instance.file;

public class InstanceWriteException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1399166319564097433L;

	public InstanceWriteException(Exception e){
		super(e);
	}
	
	public InstanceWriteException(String s){
		super(s);
	}
}
