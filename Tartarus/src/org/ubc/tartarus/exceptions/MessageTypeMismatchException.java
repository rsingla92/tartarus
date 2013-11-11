package org.ubc.tartarus.exceptions;

public class MessageTypeMismatchException extends Exception {

	private static final long serialVersionUID = -4966753371347544111L;

	public MessageTypeMismatchException() {
		super();
	}
	
	public MessageTypeMismatchException(String message) {
		super(message);
	}
	
	public MessageTypeMismatchException(String message, Throwable reason) {
		super(message, reason);
	}
	
	public MessageTypeMismatchException(Throwable reason) {
		super(reason);
	}
}
