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
package ca.n4dev.aegaeon.server.controller;

import ca.n4dev.aegaeon.server.service.InstrospectService;
import ca.n4dev.aegaeon.server.view.IntrospectResponseView;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * IntrospectController.java
 *
 * Introspect controller used to verify a token.
 *
 * @author by rguillemette
 * @since Aug 10, 2017
 */
@Controller
@RequestMapping(value = IntrospectController.URL)
@ConditionalOnProperty(prefix = "aegaeon.modules", name = "introspect", havingValue = "true", matchIfMissing = false)
public class IntrospectController {

    public static final String URL = "/introspect";

    private InstrospectService instrospectService;

    /**
     * Default Constructor.
     * @param pInstrospectService The service to introspect token.
     */
    public IntrospectController(InstrospectService pInstrospectService) {
        this.instrospectService = pInstrospectService;
    }

    /**
     * Introspect (verify) a token to know if it is still valid.
     * @param pAuthentication The client authentication object.
     * @return An Introspect response json object.
     */
    @PostMapping("")
    @ResponseBody
    public ResponseEntity<IntrospectResponseView> introspect(
            @RequestParam(value = "token", required = false) String pToken,
            @RequestParam(value = "token_hint", required = false) String pTokenHint, // ignored currently
            @RequestParam(value = "agent_of_client_id", required = false) String pAgentOfClientId,
            Authentication pAuthentication) {

        IntrospectResponseView response = this.instrospectService.introspect(pToken,
                                                                             pTokenHint,
                                                                             pAgentOfClientId,
                                                                             pAuthentication);

        return ResponseEntity.ok(response);
    }
}
