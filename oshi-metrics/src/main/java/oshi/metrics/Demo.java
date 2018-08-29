/**
 * Oshi (https://github.com/oshi/oshi)
 *
 * Copyright (c) 2010 - 2018 The Oshi Project Team
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Maintainers:
 * dblock[at]dblock[dot]org
 * widdis[at]gmail[dot]com
 * enrico.bianchi[at]gmail[dot]com
 *
 * Contributors:
 * https://github.com/oshi/oshi/graphs/contributors
 */
package oshi.metrics;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.CachedGauge;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;

public class Demo {
    private static final Logger LOG = LoggerFactory.getLogger(Demo.class);

    public static final MetricRegistry METRICS = new MetricRegistry();

    private static final String PACKAGE_PREFIX = "oshi.metrics.";
    private static final Class<?>[] NO_PARAMS = new Class[] {};
    private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS = new HashMap<>();
    static {
        PRIMITIVES_TO_WRAPPERS.put(boolean.class, Boolean.class);
        PRIMITIVES_TO_WRAPPERS.put(byte.class, Byte.class);
        PRIMITIVES_TO_WRAPPERS.put(char.class, Character.class);
        PRIMITIVES_TO_WRAPPERS.put(double.class, Double.class);
        PRIMITIVES_TO_WRAPPERS.put(float.class, Float.class);
        PRIMITIVES_TO_WRAPPERS.put(int.class, Integer.class);
        PRIMITIVES_TO_WRAPPERS.put(long.class, Long.class);
        PRIMITIVES_TO_WRAPPERS.put(short.class, Short.class);
        PRIMITIVES_TO_WRAPPERS.put(void.class, Void.class);
    }

    // RefreshMap: basically the same thing as the properties object with
    // all the casting and parsing done.
    private static final Map<String, Integer> PROPS_MAP = new HashMap<>();

    public static void main(String[] args) {
        // Options: ERROR > WARN > INFO > DEBUG > TRACE
        System.setProperty("org.slf4j.impl.SimpleLogger.defaultLogLevel", "TRACE");
        System.setProperty(org.slf4j.impl.SimpleLogger.LOG_FILE_KEY, "System.err");
        // Demonstrates a Properties-File based front end to put OSHI info in a
        // Metrics Registry and interface with POJO objects using reflection

        // Load properties from this file on the classpath
        Properties props = PropertiesUtil.loadProperties("oshi.metrics.properties");

        // Iterate levels of dotted notation to determine tree structure.
        // Last field of dotted notation is instance variable
        // Second to last field of dotted notation is POJO Class
        // All preceding fields are package

        // Put properties and refresh times in a map for fast reference
        propsToMap(props);

        // Set up Metrics Registry
        registerMetrics();

        // Report
        startReport();

        // Stall for output
        stall(5);
    }

    private static void propsToMap(Properties props) {
        PROPS_MAP.clear();
        for (Object key : props.keySet()) {
            String property = (String) key;
            int value = PropertiesUtil.parseInt(props, property);
            PROPS_MAP.put(property, value);
            LOG.trace("Refresh for {} is {}", property, value);
        }
    }

    private static void registerMetrics() {
        for (Entry<String, Integer> entry : PROPS_MAP.entrySet()) {
            int timeout = entry.getValue();
            if (timeout < 1) {
                timeout = Integer.MAX_VALUE;
            }
            String property = entry.getKey();
            String className = property.substring(0, property.lastIndexOf('.'));
            String fieldName = property.substring(property.lastIndexOf('.') + 1);
            try {
                Class<?> clazz = Class.forName(PACKAGE_PREFIX + className);
                Field f = clazz.getField(fieldName);
                registerMetric(property, clazz, fieldName, wrap(f.getType()), timeout);
            } catch (ClassNotFoundException e) {
                LOG.error("Couldn't find class {}{}: {}", PACKAGE_PREFIX, className, e);
            } catch (InstantiationException | IllegalAccessException e) {
                LOG.error("Couldn't instantiate class {}{}: {}", PACKAGE_PREFIX, className, e);
            } catch (NoSuchFieldException | SecurityException e) {
                LOG.error("Couldn't find or access field {} in class {}{}: {}", fieldName, PACKAGE_PREFIX, className,
                        e);
            }
        }
    }

    private static <T> void registerMetric(final String property, Class<?> clazz, final String fieldName,
            final Class<T> returnType, int timeout)
            throws InstantiationException, IllegalAccessException {
        final Object o = clazz.newInstance();
        METRICS.register(property, new CachedGauge<T>(timeout, TimeUnit.SECONDS) {
            @Override
            protected T loadValue() {
                try {
                    Method m = o.getClass().getMethod(getterName(fieldName), NO_PARAMS);
                    return returnType.cast(m.invoke(o));
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                    LOG.error("Error invoking {}", getterName(fieldName));
                } catch (ClassCastException cc) {
                    LOG.error("Config file return type error for {}: {}", property, cc.getMessage());
                }
                return null;
            }
        });
    }

    private static String getterName(String field) {
        StringBuilder sb = new StringBuilder("get");
        if (field.length() > 0) {
            sb.append(field.substring(0, 1).toUpperCase());
            sb.append(field.substring(1));
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> wrap(Class<T> c) {
        return c.isPrimitive() ? (Class<T>) PRIMITIVES_TO_WRAPPERS.get(c) : c;
    }

    private static void startReport() {
        ConsoleReporter reporter = ConsoleReporter.forRegistry(METRICS).convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS).build();
        reporter.start(1, TimeUnit.SECONDS);
    }

    private static void stall(int secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException e) {
            LOG.trace("Interrupted sleep.");
        }
    }

}
