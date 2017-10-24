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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Logger;

public class PropertiesService implements PropertiesResourceProvider {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private Properties properties;

    // The raw original properties from the resources.xml
    // resource entry. If "properties" has no setter,
    // TomEE you inject the values direclty into the "properties" property.
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public PropertiesService() {
        logger.info("PropertiesService created.");
    }

    @Override
    public Properties provides() {
        Properties result = new Properties();
        Cache cache = new Cache();
        for (String key : properties.stringPropertyNames()) {
            logger.info("Loading property '" + key + "'");
            String value = properties.getProperty(key);
            logger.info("        value: '" + value + "'");
            URI uri = null;
            try {
                uri = new URI(value);
                uri.toURL();
            } catch (MalformedURLException | URISyntaxException | IllegalArgumentException e) {
                uri = null;
            }
            if (uri != null) {
                try (InputStream is = cache.getContent(uri)) {
                    if (uri.getFragment() == null) {
                        Scanner s = new Scanner(is).useDelimiter("\\A");
                        value = s.hasNext() ? s.next() : "";
                    } else {
                        Properties remoteProps = new Properties();
                        remoteProps.load(is);
                        value = remoteProps.getProperty(uri.getFragment());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            result.setProperty(key, value);
        }
        return result;
    }

    private class Cache {
        Map<String, String> loadedContent = new HashMap<>();

        InputStream getContent(URI uri) throws IOException {
            URL url = uri.toURL();
            String key = url.getPath();
            String value = loadedContent.get(key);
            if (value == null) {
                logger.info("Loading URI '" + key + "'");
                try (InputStream is = url.openStream()) {
                    Scanner s = new Scanner(is).useDelimiter("\\A");
                    value = s.hasNext() ? s.next() : "";
                    loadedContent.put(key, value);
                }
            }
            return new ByteArrayInputStream(
                    value.getBytes(StandardCharsets.UTF_8.name())
            );
        }
    }

}
