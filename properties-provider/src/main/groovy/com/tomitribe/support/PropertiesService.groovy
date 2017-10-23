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
package com.tomitribe.support

import org.apache.openejb.api.resource.PropertiesResourceProvider

import java.util.logging.Logger

class PropertiesService implements PropertiesResourceProvider {

    Logger logger = Logger.getLogger(this.class.name)

    Properties properties

    @Override
    Properties provides() {
        def result = new Properties()
        properties.propertyNames().each { String key ->
            String value = properties.getProperty(key)
            if (value.matches('^(exec:).+$')) {
                // If the resource value starts with "exec:"...
                // ... run the string after it as a command and use the result as the new value.
                String cmd = value.replaceAll('^(exec:)', '')
                logger.info("Executing command '$cmd'")
                value = cmd.execute().text.trim()
            }
            result.setProperty(key, value)
        }
        return result
    }

}
