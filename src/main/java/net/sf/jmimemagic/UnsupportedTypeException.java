/*
jMimeMagic (TM) is a Java Library for determining the content type of files or streams
Copyright (C) 2003-2017 David Castro
*/
package net.sf.jmimemagic;

/**
 * JMimeMagic unsupported test type exception. This exception is thrown when an unsupported
 * test type is specified in a mime magic test
 *
 * @author $Author: arimus $
 * @version $Revision: 1.1 $
 */
public class UnsupportedTypeException extends Exception
{
    /**
     * Default constructor
     */
    public UnsupportedTypeException()
    {
        super();
    }

    /**
     * Create exception with error message
     * 
     * @param message The error message for this exception
     */
    public UnsupportedTypeException(String message)
    {
        super(message);
    }

    /**
     * Create exception based on an existing Throwable
     * 
     * @param cause The throwable on which we'll base this exception
     */
    public UnsupportedTypeException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Create an exception with custom message and throwable info
     * 
     * @param message The message
     * @param cause The target Throwable
     */
    public UnsupportedTypeException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
