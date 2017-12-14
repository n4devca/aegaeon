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
package ca.n4dev.aegaeon.server.controller;

import java.util.List;
import java.util.function.BiFunction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.ClientScope;
import ca.n4dev.aegaeon.api.model.GrantType;
import ca.n4dev.aegaeon.api.model.Scope;
import ca.n4dev.aegaeon.server.controller.dto.PageDto;
import ca.n4dev.aegaeon.server.controller.dto.PageListDto;
import ca.n4dev.aegaeon.server.service.ClientService;
import ca.n4dev.aegaeon.server.service.GrantTypeService;
import ca.n4dev.aegaeon.server.service.ScopeService;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.ClientView;
import ca.n4dev.aegaeon.server.view.ClientViewAction;
import ca.n4dev.aegaeon.server.view.SelectableItemView;

/**
 * SimpleClientAdminController.java
 * 
 * A controller to admisnister Clients.
 *
 * @author by rguillemette
 * @since Oct 24, 2017
 */
@Controller
@RequestMapping(value = SimpleClientAdminController.URL)
@ConditionalOnProperty(prefix = "aegaeon.modules", name = "admin", havingValue = "true", matchIfMissing = false)
public class SimpleClientAdminController extends BaseUiController {

    public static final String URL = "/admin/clients";
    public static final String VIEW_LIST = "/admin/client-list";
    public static final String VIEW_EDIT = "/admin/client-edit";
    
    private ClientService clientService;
    private ScopeService scopeService;
    private GrantTypeService grantTypeService;
    
    
    /**
     * @param pMessages
     */
    @Autowired
    public SimpleClientAdminController(ClientService pClientService, ScopeService pScopeService, GrantTypeService pGrantTypeService, MessageSource pMessages) {
        super(pMessages);
        this.clientService = pClientService;
        this.scopeService = pScopeService;
        this.grantTypeService = pGrantTypeService;
    }

    @GetMapping("")
    public ModelAndView index(Pageable pPageable) {
        ModelAndView mv = new ModelAndView(VIEW_LIST);
        
        // Get Clients 
        PageDto<ClientView> clients = this.clientService.findByPage(pPageable);
        mv.addObject("pageList", clients);
        
        return mv;
    }
    
    @GetMapping("/{clientid}")
    public ModelAndView getOne(@PathVariable("clientid") Long pId) {

        ModelAndView mv = getEditViewAndDependencies();
        ClientView dto = this.clientService.findOne(pId);
        mv.addObject("client", dto);
        
        return mv;
    }
    
    @PostMapping("/{clientid}")
    public ModelAndView postOne(@PathVariable("clientid") Long pId,
                                @RequestParam(value = "action", required = false, defaultValue = "save") String pAction,
                                @ModelAttribute("client") ClientView pClientDto) {
        
        ModelAndView mv = getEditViewAndDependencies();
        
        // TODO(RG) : catch error and show alert
        if (pAction.startsWith("action_")) {
            doAction(pClientDto, pAction);            
        } else {
            // Save
            
        }
        
        mv.addObject("client", pClientDto);
        
        return mv;
    }
    
    private void doAction(ClientView pClientDto, String pAction) {
        String actionStr = pAction;
        Integer idx = null;
        if (pAction.contains(".")) {
            String[] actionData = pAction.split("\\.");
            
            if (actionData == null || actionData.length != 2) {
                throw new ServerException(ServerExceptionCode.INVALID_PARAMETER);
            }
            actionStr = actionData[0];
            // TODO(RG) Manage exception
            idx = Integer.parseInt(actionData[1]);
        }
            
        ClientViewAction action = ClientViewAction.from(actionStr);
        
        
        if (action != null) {
            
            switch (action) {
            case action_add_contact:
                pClientDto.getContacts().add("");
                break;
            case action_add_redirect_url:
                pClientDto.getRedirections().add("https://");

            case action_remove_contact:
                pClientDto.getContacts().remove(idx);
                break;
            case action_remove_redirect_url:
                pClientDto.getRedirections().remove(idx);
                break;
            default:
                break;
            }
            
        }
    }
    
    private ModelAndView getEditViewAndDependencies() {
        ModelAndView mv = new ModelAndView(VIEW_EDIT);
        
        List<GrantType> grants = this.grantTypeService.findAll();
        List<Scope> scopes = this.scopeService.findAll();
        
        mv.addObject("scopes", Utils.convert(scopes, s -> new SelectableItemView(s.getId(), s.getName(), null, false)));
        mv.addObject("grants", Utils.convert(grants, g -> new SelectableItemView(g.getId(), g.getCode(), null, false)));
        
        return mv;
    }
}
