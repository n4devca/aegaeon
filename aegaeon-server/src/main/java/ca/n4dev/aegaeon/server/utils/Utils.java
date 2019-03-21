/**
 * Copyright 2017 Remi Guillemette - n4dev.ca
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package ca.n4dev.aegaeon.server.utils;

import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ObjectUtils.java
 * <p>
 * Static functions to check various state of object (empty, null, etc)
 *
 * @author by rguillemette
 * @since May 22, 2017
 */
public class Utils {

    public static final String SPACE = " ";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    private static final AtomicLong counter = new AtomicLong(System.currentTimeMillis());

    public static boolean isEmpty(String pValue) {
        return pValue == null || pValue.isEmpty();
    }

    public static boolean isNotEmpty(String pValue) {
        return pValue != null && !pValue.isEmpty();
    }

    public static boolean isEmpty(Collection<?> pCollection) {
        return pCollection == null || pCollection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> pCollection) {
        return pCollection != null && !pCollection.isEmpty();
    }

    public static <E> boolean isNotEmptyThen(Collection<E> pCollection, Consumer<Collection<E>> pThen) {
        boolean toConsume = isNotEmpty(pCollection);

        if (toConsume) {
            pThen.accept(pCollection);
        }

        return toConsume;
    }

    public static <E> boolean equals(E pEntity1, E pEntity2) {
        if (pEntity1 != null) {
            return pEntity1.equals(pEntity2);
        }

        return false;
    }

    public static boolean areOneEmpty(Object... pValues) {

        if (pValues != null) {

            for (Object v : pValues) {
                if (v instanceof String && isEmpty((String) v)) {
                    return true;
                } else if (v == null) {
                    return true;
                }
            }

            return false;
        }

        return true;
    }

    public static boolean isAfterNow(LocalDateTime pValidUntil) {
        if (pValidUntil != null) {
            LocalDateTime now = LocalDateTime.now();
            return pValidUntil.isAfter(now);
        }

        return true;
    }

    public static boolean isAfterNow(ZonedDateTime pValidUntil) {
        if (pValidUntil != null) {
            ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
            return pValidUntil.isAfter(now);
        }

        return true;
    }

    public static <E, T> List<T> convert(List<E> pElements, Function<E, T> pFunc) {

        List<T> l = new ArrayList<>();

        if (pElements != null) {
            for (E e : pElements) {
                l.add(pFunc.apply(e));
            }
        }

        return l;
    }


    public static <E, T> Set<T> convert(Set<E> pElements, Function<E, T> pFunc) {


        if (pElements != null) {
            Set<T> l = new HashSet<>();
            for (E e : pElements) {
                l.add(pFunc.apply(e));
            }
            return l;
        }

        return Collections.emptySet();
    }

    public static <E, M, T> List<T> convert(List<E> pElements, M pModel, BiFunction<E, M, T> pFunc) {

        List<T> l = new ArrayList<>();

        if (pElements != null) {
            for (E e : pElements) {
                l.add(pFunc.apply(e, pModel));
            }
        }

        return l;
    }

    public static <E> String join(Collection<E> pElements, Function<E, String> pFunc) {
        return join(SPACE, pElements, pFunc);
    }

    public static <E> String join(String pSeparator, Collection<E> pElements, Function<E, String> pFunc) {
        if (pElements == null || pElements.isEmpty()) {
            return "";
        }

        StringBuilder b = new StringBuilder();
        boolean first = true;

        for (E e : pElements) {
            if (!first) {
                b.append(pSeparator);
            }

            b.append(pFunc.apply(e));
            first = false;
        }

        return b.toString();
    }

    public static <E> List<E> explode(String pElementsStr, Function<String, E> pFunc) {
        return explode(SPACE, pElementsStr, pFunc);
    }

    public static <E> List<E> explode(String pSeparator, String pElementsStr, Function<String, E> pFunc) {
        if (pElementsStr == null || pElementsStr.isEmpty()) {
            return Collections.emptyList();
        }

        String[] els = pElementsStr.split(pSeparator);
        List<E> lst = new ArrayList<>();

        for (String e : els) {
            if (isNotEmpty(e)) {
                lst.add(pFunc.apply(e));
            }
        }

        return lst;
    }

    public static <E> Set<E> safeSet(Set<E> pSet) {

        if (pSet == null) {
            return Collections.emptySet();
        }

        return pSet;
    }

    public static <O> O coalesce(O... pEntities) {
        if (pEntities != null) {
            for (O e : pEntities) {
                if (e != null) {
                    return e;
                }
            }
        }

        return null;
    }

    public static <E> boolean contains(E[] pEntities, E pValue) {
        if (pEntities != null) {
            for (E e : pEntities) {
                if (e.equals(pValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static <E> boolean contains(List<E> pEntities, E pValue) {
        if (pEntities != null) {
            for (E e : pEntities) {
                if (e.equals(pValue)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static <E> boolean isOneTrue(Collection<E> pEntities, Function<E, Boolean> pSearchFunc) {
        if (pEntities != null) {
            for (E e : pEntities) {
                if (pSearchFunc.apply(e)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Map<String, String> asMap(String... pKeyValues) {
        return asMap(false, pKeyValues);
    }

    public static Map<String, String> asMap(boolean pSkipNullValue, String... pKeyValues) {
        Map<String, String> p = new LinkedHashMap<>();

        if (pKeyValues != null) {

            if (pKeyValues.length % 2 != 0) {
                throw new ServerException(new ArrayIndexOutOfBoundsException());
            }

            for (int i = 0, j = pKeyValues.length; i < j; i += 2) {
                if (!pSkipNullValue || pKeyValues[i + 1] != null) {
                    p.put(pKeyValues[i], pKeyValues[i + 1]);
                }
            }
        }

        return p;
    }

    /**
     * return a String from the input.
     * If the object is not null, it will be convert to string, else an empty string is returned.
     *
     * @param pObject The object to convert.
     * @return A String.
     */
    public static String asString(Object pObject) {
        if (pObject != null) {
            return pObject.toString();
        }
        return "";
    }

    public static <E> List<E> asList(E... pEntities) {

        if (pEntities != null) {
            List<E> entities = new ArrayList<>();
            for (E entity : pEntities) {
                entities.add(entity);
            }
            return entities;
        }

        return Collections.emptyList();
    }

    public static <E> Set<E> asSet(E... pEntities) {

        if (pEntities != null) {
            Set<E> entities = new HashSet<>();
            for (E entity : pEntities) {
                entities.add(entity);
            }
            return entities;
        }

        return Collections.emptySet();
    }

    /**
     * Get a positive id.
     *
     * @return next positive id.
     */
    public static Long nextPositiveId() {
        return counter.incrementAndGet();
    }

    /**
     * Raise a {@link ServerException}
     *
     * @param pServerExceptionCode the {@link ServerExceptionCode}
     */
    @Deprecated
    public static void raise(ServerExceptionCode pServerExceptionCode) {
        raise(pServerExceptionCode, null);
    }

    /**
     * Raise a {@link ServerException}
     *
     * @param pServerExceptionCode the {@link ServerExceptionCode}
     * @param pMessage             A message.
     */
    @Deprecated
    public static void raise(ServerExceptionCode pServerExceptionCode, String pMessage) {
        if (pServerExceptionCode != null) {
            throw new ServerException(pServerExceptionCode, pMessage);
        }
    }

    public static <E, O> Differentiation<E> differentiate(List<E> pOriginalList, List<O> pOtherList, BiFunction<E, O, Boolean> pFindFunc,
                                                          BiFunction<E, O, E> pUpdater, Function<O, E> pCreator) {
        //Pair<List<E>, List<E>> reponse = new Pair<>();
        List<E> newObjs = new ArrayList<>();
        List<E> updatedObjs = new ArrayList<>();
        List<E> removedObjs = new ArrayList<>();

        // Update and entities to remove
        for (E original : pOriginalList) {

            boolean found = false;

            for (O other : pOtherList) {

                // Found
                if (pFindFunc.apply(original, other)) {
                    updatedObjs.add(pUpdater.apply(original, other));
                    found = true;
                    break;
                }
            }

            // Not found => to remove
            if (!found) {
                removedObjs.add(original);
            }
        }

        // New entities
        for (O other : pOtherList) {
            boolean found = false;

            for (E original : pOriginalList) {
                if (pFindFunc.apply(original, other)) {
                    found = true;
                    break;
                }
            }

            // new entities
            if (!found) {
                newObjs.add(pCreator.apply(other));
            }
        }

        return new Differentiation<>(newObjs, updatedObjs, removedObjs);
    }

    public static <V, E, C> List<V> combine(List<C> pManagedList,
                                            List<E> pOriginalList,
                                            BiFunction<C, E, Boolean> pEqualsFunc,
                                            Function<E, V> pCreatorFunc,
                                            Function<C, V> pAddFunc) {
        List<V> combined = new ArrayList<>();

        for (E original : pOriginalList) {
            boolean found = false;
            for (C managed : pManagedList) {
                if (pEqualsFunc.apply(managed, original)) {
                    combined.add(pAddFunc.apply(managed));
                    found = true;
                    break;
                }
            }

            if (!found) {
                combined.add(pCreatorFunc.apply(original));
            }
        }

        return combined;
    }

    public static boolean validateRedirectionUri(String pUri) {

        try {

            if (isNotEmpty(pUri)) {
                URL url = new URL(pUri);
                URI uri = url.toURI();

                // No param or fragment allowed
                if (isNotEmpty(uri.getFragment()) || isNotEmpty(uri.getQuery())) {
                    return false;
                }

                // Only http(s)
                if (!uri.getScheme().equals("http") && !uri.getScheme().equals("https")) {
                    return false;
                }

                // Empty host ?
                if (isEmpty(uri.getHost())) {
                    return false;
                }

                // http is only allowed with localhost/127.0.0.1
                boolean isLocalhost = uri.getHost().equals("localhost") || uri.getHost().equals(("127.0.0.1"));

                if (uri.getScheme().equals("http") && !isLocalhost) {
                    return false;
                }

                // Make sure we have a tld
                if (!isLocalhost && uri.getHost().split("\\.").length <= 1) {
                    return false;
                }

                // Make sure we don't have /./ and /../
                if (isNotEmpty(uri.getPath())) {
                    if (uri.getPath().contains("/.")) {
                        return false;
                    }
                }

                return true;
            }

        } catch (Exception e) {
            LOGGER.warn("Error validating Url: " + pUri, e);
        }

        // return false to be sure
        return false;
    }


}
