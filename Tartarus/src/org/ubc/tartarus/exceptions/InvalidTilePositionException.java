package org.ubc.tartarus.exceptions;

public class InvalidTilePositionException extends Exception {

	private static final long serialVersionUID = 7611339881149356620L;

	public InvalidTilePositionException() {
		super();
	}
	
	public InvalidTilePositionException(String message) {
		super(message);
	}
	
	public InvalidTilePositionException(String message, Throwable reason) {
		super(message, reason);
	}
	
	public InvalidTilePositionException(Throwable reason) {
		super(reason);
	}
}