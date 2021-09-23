package com.huahui.datasphere.portal.ExceptionHandling;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.hadoop.hive.metastore.HiveMetaException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.metadata.HiveFatalException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.serde2.SerDeException;

import com.huahui.datasphere.portal.config.DSSConstants;


public class DataSphereRunTimeException extends RuntimeException
{
    private String errorMessage;
    private String errorIdentifier;
    private Throwable propagatedException;
    private String detailedErrorMessage;
    private String errorCode;
    private static Properties properties;
    
    private static String getMinedErrorMessage(final Throwable e) {
        if (e != null) {
            String errorMessage = ExceptionUtils.getMessage(e);
            if (e instanceof HiveFatalException) {
                errorMessage = ((HiveFatalException)e).getCanonicalErrorMsg().getMsg();
            }
            else if (e instanceof UDFArgumentException) {
                errorMessage = e.getLocalizedMessage();
                errorMessage += "\\nUDF TypeList proceeds :";
                errorMessage += ((UDFArgumentException)e).getArgTypeList().toString();
            }
            else if (e instanceof SerDeException) {
                final SerDeException serDeException = (SerDeException)e;
                errorMessage = serDeException.getMessage();
            }
            else if (e instanceof SemanticException) {
                final SemanticException ex = (SemanticException)e;
                errorMessage = ex.getCanonicalErrorMsg().getErrorCodedMsg();
            }
            else if (e instanceof HiveMetaException) {
                errorMessage = e.getLocalizedMessage();
            }
            else if (e instanceof HiveException) {
                errorMessage = ((HiveException)e).getCanonicalErrorMsg().getErrorCodedMsg();
            }
            else if (e instanceof NullPointerException) {
                errorMessage = "Processing null values at the time of job execution";
            }
            return errorMessage;
        }
        return null;
    }
    
    public DataSphereRunTimeException(final String errorIdentifier, final Throwable propagatedException) {
        this.errorIdentifier = StringUtils.substringBetween(errorIdentifier, "{", "}");
        if (this.isErrorPresent(this.errorIdentifier)) {
            this.errorCode = this.getErrorCode(this.errorIdentifier);
            this.errorMessage = this.getErrorMessage(this.errorIdentifier);
            this.propagatedException = propagatedException;
            this.detailedErrorMessage = getMinedErrorMessage(propagatedException);
        }
        else {
            this.errorCode = "0000";
            this.errorMessage = "An error occurred while executing job";
            this.propagatedException = propagatedException;
            this.detailedErrorMessage = getMinedErrorMessage(propagatedException);
        }
    }
    
    public String getErrorMessage() {
        return this.errorMessage;
    }
    
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getErrorIdentifier() {
        return this.errorIdentifier;
    }
    
    public void setErrorIdentifier(final String errorIdentifier) {
        this.errorIdentifier = errorIdentifier;
    }
    
    public Throwable getPropagatedException() {
        return this.propagatedException;
    }
    
    public void setPropagatedException(final Throwable propagatedException) {
        this.propagatedException = propagatedException;
    }
    
    public String getDetailedErrorMessage() {
        return this.detailedErrorMessage;
    }
    
    public void setDetailedErrorMessage(final String detailedErrorMessage) {
        this.detailedErrorMessage = detailedErrorMessage;
    }
    
    public String getErrorCode() {
        return this.errorCode;
    }
    
    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getErrorCode(final String errorIdentifier) {
        final Object object = DataSphereRunTimeException.properties.get(errorIdentifier);
        final String[] errorSpecification = object.toString().split(",");
        return errorSpecification[0];
    }
    
    public String getErrorMessage(final String errorIdentifier) {
        final Object object = DataSphereRunTimeException.properties.get(errorIdentifier);
        final String[] errorSpecification = object.toString().split(",");
        return errorSpecification[1];
    }
    
    public boolean isErrorPresent(final String errorIdentifier) {
        return !StringUtils.isEmpty(errorIdentifier) && DataSphereRunTimeException.properties.containsKey(errorIdentifier);
    }
    
    public static void propagate(final Exception e, final String errorIdentifier) throws DataSphereRunTimeException {
        if (e instanceof DataSphereRunTimeException) {
            throw (DataSphereRunTimeException)e;
        }
        if (errorIdentifier != null) {
            throw new DataSphereRunTimeException("{" + errorIdentifier + "}", e);
        }
        throw new DataSphereRunTimeException(String.format("{%s}", "JOB_EXECUTION_ERROR"), e);
    }
    
    @Override
    public String getMessage() {
        return "[" + this.errorIdentifier + ": " + this.errorCode + "]: " + this.errorMessage;
    }
    
    static {
        DataSphereRunTimeException.properties = null;
        try {
            final String path = IWPathUtil.getErrorConfPath();
            final File file = new File(path);
            if (!file.exists()) {
                Files.write(Paths.get(path, new String[0]), DSSConstants.IW_ERROR_CONF_DEFAULT_ERROR_MESSAGE.getBytes(), new OpenOption[0]);
            }
            final FileInputStream fileInput = new FileInputStream(file);
            (DataSphereRunTimeException.properties = new Properties()).load(fileInput);
            fileInput.close();
        }
        catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
