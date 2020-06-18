package net.optionfactory.tomcat9.lerv;

import java.util.logging.Logger;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ErrorReportValve;

public class LoggingErrorReportValve extends ErrorReportValve {

    private static final Logger LOGGER = Logger.getLogger("error-report-valve");
    
    @Override
    protected void report(Request request, Response response, Throwable throwable) {
        final int statusCode = response.getStatus();
        if(statusCode < 400 || throwable == null){
            return;
        }
        LOGGER.warning(String.format("[m:%s][u:%s][s:%s] %s: %s", request.getMethod(), request.getRequestURI(), statusCode, throwable.getClass(), throwable.getMessage()));
    }

}
