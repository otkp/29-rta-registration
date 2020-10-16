package org.epragati.aadhar.exception;

/**
 * 
 * @author pbattula
 *
 */

public class NotFoundException extends Exception {

	private static final long serialVersionUID = 4769043535599151796L;

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }
}
