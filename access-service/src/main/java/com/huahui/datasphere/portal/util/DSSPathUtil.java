package com.huahui.datasphere.portal.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.huahui.datasphere.portal.ExceptionHandling.DataSphereException;
import com.huahui.datasphere.portal.config.DSSConstants;
import com.huahui.datasphere.portal.configuration.DSSConf;
import com.mongodb.DBObject;


public class DSSPathUtil
{
    private static Logger LOGGER;
    
    private static String checkIfNonNull(final String path) {
        return (String)Preconditions.checkNotNull(path, "Path cannot be null");
    }
    
    public static String addSeparatorIfNotExists(String path) {
        checkIfNonNull(path);
        if (!path.endsWith("/")) {
            path += '/';
        }
        return path;
    }
    
    public static final String getDSSHome() {
        String appConf = System.getenv("DSS_HOME");
        if (appConf == null) {
            appConf = System.getProperty("DSS_HOME");
            if (appConf == null) {
                throw new AssertionError("DSS_HOME env variable should point to application home dir. Please set it in /etc/profile and login again.");
            }
        }
        return addSeparatorIfNotExists(appConf);
    }
    
    public static final String getIWConfHome() {
        return addSeparatorIfNotExists(getDSSHome() + "conf");
    }
    
    public static final String getIWManifestFilePath() {
        return getDSSHome() + "manifest.json";
    }
    
    public static final String getIWTempHome() {
        return addSeparatorIfNotExists(getDSSHome() + "temp");
    }
    
    public static final String getIWBSONTempHome() {
        return addSeparatorIfNotExists(getIWTempHome() + "bsondump");
    }
    
   
    
    
   
  
    
    public static String getIWBinPath() {
        return addSeparatorIfNotExists(getDSSHome() + "bin");
    }
    
    public static List<String> recursiveListJars(final List<String> inputDirs, final boolean silent) {
        final List<String> jarFiles = Lists.newArrayList();
        for (final String dep : inputDirs) {
            if (dep.endsWith("jar")) {
                jarFiles.add(dep);
            }
            else if (dep.endsWith("/*")) {
                final String folder = dep.substring(0, dep.length() - 1);
                final File[] files = new File(folder).listFiles();
                if (files == null) {
                    DSSPathUtil.LOGGER.warn("Unable to find files under {}", folder);
                    if (!silent) {
                        throw new DataSphereException("Unable to find files under {}" + folder);
                    }
                    continue;
                }
                else {
                    for (final File f : files) {
                        if (f.isFile() && f.getAbsolutePath().endsWith("jar")) {
                            jarFiles.add(f.getAbsolutePath());
                        }
                    }
                }
            }
            else {
                DSSPathUtil.LOGGER.warn("Invalid dependency " + dep);
                if (!silent) {
                    throw new DataSphereException("Invalid dependency " + dep);
                }
                continue;
            }
        }
        return jarFiles;
    }
    
    
    
    public static String getIWLibPath() {
        final String libPath = getDSSHome() + "lib/";
        return addSeparatorIfNotExists(libPath);
    }
    
    public static String getMongoDumpPath(final ObjectId sourceId, final ObjectId tableId) {
        final String MongoDumpPath = getDSSHome() + "mongodump/" + sourceId + "/" + tableId + "/";
        return addSeparatorIfNotExists(MongoDumpPath);
    }
    
 
    
   
    
    public static String getErrorConfPath() {
        return addSeparatorIfNotExists(getIWConfHome()) + "error.conf.properties";
    }
    
    public static Path getOrCreatePath(final String pathString, final FileSystem fs) throws IOException {
        final Path path = new Path(pathString);
        if (!fs.exists(path)) {
            fs.mkdirs(path);
        }
        return path;
    }
    
    
    
    
    static {
        DSSPathUtil.LOGGER = LoggerFactory.getLogger(DSSPathUtil.class);
    }
}
