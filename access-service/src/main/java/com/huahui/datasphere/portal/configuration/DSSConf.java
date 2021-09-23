/*
 * Apache License
 * 
 * Copyright (c) 2021 HuahuiData
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huahui.datasphere.portal.configuration;

import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.google.common.base.Throwables;
import com.huahui.datasphere.portal.util.DSSPathUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


public final class DSSConf
{
    private static PropertiesConfiguration config;
    private static DBObject dbConf;
    private static DSSConf INSTANCE;
    private static Object lock;
    
    public static PropertiesConfiguration getStaticConf() {
        if (DSSConf.config != null) {
            return DSSConf.config;
        }
        synchronized (DSSConf.lock) {
            try {
                final String appConfHome = DSSPathUtil.getIWConfHome();
                final IWFileSystem fs = FileSystemFactory.getFileSystem(appConfHome);
                (DSSConf.config = new PropertiesConfiguration()).load(fs.open(appConfHome + "conf.properties"));
                fs.close();
            }
            catch (ConfigurationException | IOException e) {
                e.printStackTrace();
                System.out.println("error during config : " + Throwables.getStackTraceAsString((Throwable)e));
            }
        }
        if (DSSConf.config == null) {
            System.out.println("unable to load config");
        }
        return DSSConf.config;
    }
    
    private DSSConf() {
        if (DSSConf.dbConf == null) {
            getStaticConf();
            final BasicDBObject query = new BasicDBObject();
            query.put("name", "iw_conf_default");
            final DBObject dbObject = store.findOneWithoutSqlValidation("configs", (DBObject)query);
            if (dbObject != null && dbObject.containsField("configs")) {
                DSSConf.dbConf = (DBObject)dbObject.get("configs");
            }
        }
    }
    
    public static DSSConf getConfig() {
        if (DSSConf.INSTANCE != null) {
            return DSSConf.INSTANCE;
        }
        synchronized (DSSConf.class) {
            if (DSSConf.INSTANCE != null) {
                return DSSConf.INSTANCE;
            }
            DSSConf.INSTANCE = new DSSConf();
        }
        return DSSConf.INSTANCE;
    }
    
    private boolean dbConfContainsKey(final String key) {
        return DSSConf.dbConf != null && DSSConf.dbConf.containsField(key);
    }
    
    public boolean containsKey(final String key) {
        return this.dbConfContainsKey(key) || getStaticConf().containsKey(key);
    }
    
    public String getStringDefaultKey(final String key, final String defKey) {
        String val = this.getString(key);
        if (val == null) {
            val = this.getString(defKey);
        }
        return val;
    }
    
    public String getString(final String key) {
        return this.getString(key, null);
    }
    
    public String getString(final String key, final String def) {
        if (this.dbConfContainsKey(key)) {
            return (String)((DBObject)DSSConf.dbConf.get(key)).get("value");
        }
        return getStaticConf().getString(key, def);
    }
    
    public Integer getInt(final String key) {
        return this.getInt(key, null);
    }
    
    public Integer getInt(final String key, final Integer def) {
        if (this.dbConfContainsKey(key)) {
            return Integer.valueOf((String)((DBObject)DSSConf.dbConf.get(key)).get("value"));
        }
        return getStaticConf().getInteger(key, def);
    }
    
    public Long getLong(final String key, final Long def) {
        if (this.dbConfContainsKey(key)) {
            return Long.valueOf((String)((DBObject)DSSConf.dbConf.get(key)).get("value"));
        }
        return getStaticConf().getLong(key, def);
    }
    
    public Double getDouble(final String key, final Double def) {
        if (this.dbConfContainsKey(key)) {
            return Double.valueOf((String)((DBObject)DSSConf.dbConf.get(key)).get("value"));
        }
        return getStaticConf().getDouble(key, def);
    }
    
    public Boolean getBoolean(final String key, final Boolean def) {
        if (this.dbConfContainsKey(key)) {
            return Boolean.valueOf((String)((DBObject)DSSConf.dbConf.get(key)).get("value"));
        }
        return getStaticConf().getBoolean(key, def);
    }
    
    public String[] getArray(final String key, final String[] def) {
        if (this.dbConfContainsKey(key)) {
            return ((String)((DBObject)DSSConf.dbConf.get(key)).get("value")).split(",");
        }
        final String tempVal = getStaticConf().getString(key, (String)null);
        if (tempVal == null) {
            return def;
        }
        return tempVal.split(",");
    }
    
    public static void setConfigForKafkaIngestion(final PropertiesConfiguration staticConfInput, final DBObject dbConfInput) {
        DSSConf.config = staticConfInput;
        DSSConf.dbConf = dbConfInput;
    }
    
    public static void setConfigForKafkaIngestion(final DBObject dbConfInput) {
        DSSConf.dbConf = dbConfInput;
    }
    
    public static void setConfigForTests(final PropertiesConfiguration staticConfInput, final DBObject dbConfInput) {
        DSSConf.config = staticConfInput;
        DSSConf.dbConf = dbConfInput;
    }
    
    static {
        DSSConf.config = null;
        DSSConf.dbConf = null;
        DSSConf.lock = new Object();
    }
}
