/**
 * Copyright 2017 Remi Guillemette - n4dev.ca
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package ca.n4dev.aegaeon.server.controller.validator;

import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.ClientView;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * ClientViewValidator.java
 *
 * Validate clientView before create or update.
 *
 * @author by rguillemette
 * @since Jan 12, 2018
 *
 */
@Component
public class ClientViewValidator implements Validator {

    @Override
    public boolean supports(Class<?> pClass) {
        return ClientView.class.equals(pClass);
    }

    @Override
    public void validate(Object pClientView, Errors pErrors) {
        ClientView clientView = (ClientView) pClientView;

        //ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "id", "page.adminclients.error.id.empty", "ID is required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "publicId", "page.adminclients.error.publicid.empty", "Public ID is required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "page.adminclients.error.name.empty", "Name is required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "providerType", "page.adminclients.error.providertype.empty", "You need to select one provider.");

        if (Utils.isEmpty(clientView.getRedirections())) {
            pErrors.rejectValue("redirections", "page.adminclients.error.redirections.empty");
        } else {
            for (int i = 0; i < clientView.getRedirections().size(); i++) {
                if (!Utils.validateRedirectionUri(clientView.getRedirections().get(i))) {
                    pErrors.rejectValue("redirections[" + i + "]", "page.adminclients.error.redirections.invalid");
                }
            }
        }

        if (Utils.isEmpty(clientView.getContacts())) {
            pErrors.rejectValue("contacts", "page.adminclients.error.contacts.empty");
        }
    }
}
