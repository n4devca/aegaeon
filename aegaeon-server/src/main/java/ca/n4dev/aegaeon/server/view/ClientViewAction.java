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
package ca.n4dev.aegaeon.server.view;

/**
 * ClientDtoAction.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Nov 30, 2017
 */
public enum ClientViewAction {
    action_add_contact,
    action_add_redirect_url,
    
    action_remove_contact,
    action_remove_redirect_url,
    ;
    
    public static ClientViewAction from(String pAction) {
        for (ClientViewAction cda : ClientViewAction.values()) {
            if (cda.toString().equalsIgnoreCase(pAction)) {
                return cda;
            }
        }
        return null;
    }
}
