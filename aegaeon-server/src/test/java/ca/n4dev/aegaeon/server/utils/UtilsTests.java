/*
 * *
 *  * Copyright 2017 Remi Guillemette - n4dev.ca
 *  *
 *  * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *  *
 *
 *
 */

package ca.n4dev.aegaeon.server.utils;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTests {

    @Test
    public void testUrls() {
        Assert.assertTrue("https://patate.com/login", Utils.validateRedirectionUri("https://patate.com/login"));
        Assert.assertTrue("https://bob.ca/", Utils.validateRedirectionUri("https://bob.ca/"));
        Assert.assertTrue("https://patate.com/login/inner/realm/deep", Utils.validateRedirectionUri("https://patate.com/login/inner/realm/deep"));
        Assert.assertTrue("https://patate.com:449/login", Utils.validateRedirectionUri("https://patate.com:449/login"));
        Assert.assertTrue("http://localhost/login", Utils.validateRedirectionUri("http://localhost/login"));
        Assert.assertTrue("http://127.0.0.1/login", Utils.validateRedirectionUri("http://127.0.0.1/login"));

        Assert.assertFalse("http://patate/login", Utils.validateRedirectionUri("http://patate/login"));
        Assert.assertFalse("https://patate.com:449/login#frame-html", Utils.validateRedirectionUri("https://patate.com:449/login#frame-html"));
        Assert.assertFalse("http://patate.com:449/login?param=hello", Utils.validateRedirectionUri("http://patate.com:449/login?param=hello"));
        Assert.assertFalse("//patate.com/a/../login", Utils.validateRedirectionUri("//patate.com/a/../login"));
        Assert.assertFalse("/patate.com/a/../login", Utils.validateRedirectionUri("/patate.com/a/../login"));
        Assert.assertFalse("https://patate.com/a/../login", Utils.validateRedirectionUri("https://patate.com/a/../login"));

        Assert.assertFalse("https://patate/login", Utils.validateRedirectionUri("https://patate/login"));
    }


}
