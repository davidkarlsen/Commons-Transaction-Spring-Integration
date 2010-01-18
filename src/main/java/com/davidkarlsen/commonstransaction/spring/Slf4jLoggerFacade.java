package com.davidkarlsen.commonstransaction.spring;

import org.apache.commons.transaction.util.LoggerFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A logger facade which delegates to slf4j.
 * 
 * @author karltdav
 */
public class Slf4jLoggerFacade
    implements LoggerFacade
{
    private final Logger logger;

    public Slf4jLoggerFacade()
    {
        this( LoggerFactory.getLogger( Slf4jLoggerFacade.class ) );
    }

    public Slf4jLoggerFacade( Logger logger )
    {
        this.logger = logger;
    }

    public LoggerFacade createLogger( String name )
    {
        Logger logger = LoggerFactory.getLogger( name );
        return new Slf4jLoggerFacade( logger );
    }

    public boolean isFineEnabled()
    {
        return logger.isInfoEnabled();
    }

    public boolean isFinerEnabled()
    {
        return logger.isDebugEnabled();
    }

    public boolean isFinestEnabled()
    {
        return logger.isTraceEnabled();
    }

    public void logFine( String message )
    {
        logger.info( message );
    }

    public void logFiner( String message )
    {
        logger.debug( message );
    }

    public void logFinest( String message )
    {
        logger.trace( message );
    }

    public void logInfo( String message )
    {
        logger.info( message );
    }

    public void logSevere( String message )
    {
        logger.error( message );
    }

    public void logSevere( String message, Throwable throwable )
    {
        logger.error( message, throwable );
    }

    public void logWarning( String message )
    {
        logger.warn( message );
    }

    public void logWarning( String message, Throwable throwable )
    {
        logger.warn( message, throwable );
    }

}
