/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tomitribe.support;

import org.apache.openejb.api.resource.PropertiesResourceProvider;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Logger;

public class PropertiesService implements PropertiesResourceProvider {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private Properties properties;

    // The raw original properties from the resources.xml resource entry
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Properties provides() {
        Properties result = new Properties();
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            if (value.matches("^(exec:).+$")) {
                // If the resource value starts with "exec:"...
                // ... run the string after it as a command and use the result as the new value.
                String cmd = value.replaceAll("^(exec:)", "");
                logger.info("Executing command '" + cmd + "'");
                Scanner s;
                try {
                    s = new Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                value = s.hasNext() ? s.next() : "";
            }
            result.setProperty(key, value);

        }
        return result;
    }

}
