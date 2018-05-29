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

import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.api.model.Scope;
import ca.n4dev.aegaeon.api.protocol.GrantType;
import ca.n4dev.aegaeon.server.controller.dto.PageDto;
import ca.n4dev.aegaeon.server.controller.validator.ClientViewValidator;
import ca.n4dev.aegaeon.server.service.ClientService;
import ca.n4dev.aegaeon.server.service.ScopeService;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.ClientView;
import ca.n4dev.aegaeon.server.view.ClientViewAction;
import ca.n4dev.aegaeon.server.view.SelectableItemView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SimpleClientAdminController.java
 * <p>
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

    private ClientViewValidator clientViewValidator;

    /**
     *
     * @param pClientService
     * @param pScopeService
     * @param pMessages
     * @param pClientViewValidator
     */
    @Autowired
    public SimpleClientAdminController(ClientService pClientService, ScopeService pScopeService,
                                       MessageSource pMessages, ClientViewValidator pClientViewValidator) {
        super(pMessages);
        this.clientService = pClientService;
        this.scopeService = pScopeService;
        this.clientViewValidator = pClientViewValidator;
    }

    @GetMapping("")
    public ModelAndView index(Pageable pPageable) {
        ModelAndView mv = new ModelAndView(VIEW_LIST);

        // Get Clients 
        PageDto<ClientView> clients = this.clientService.findByPage(pPageable);
        mv.addObject("pageList", clients);

        return mv;
    }

    @PostMapping("")
    public ModelAndView createOne() {

        ModelAndView mv = getEditViewAndDependencies(null);
        ClientView clientView = this.clientService.instantiateOne();
        clientView.setId(-1L);

        mv.addObject("client", clientView);

        return mv;
    }

    @GetMapping("/{clientid}")
    public ModelAndView getOne(@PathVariable("clientid") Long pId) {

        ModelAndView mv = getEditViewAndDependencies(null);
        ClientView dto = this.clientService.findOne(pId);
        mv.addObject("client", dto);

        return mv;
    }

    @PostMapping("/new")
    public ModelAndView postNewOne(@RequestParam(value = "action", required = false, defaultValue = "update") String pAction,
                                   @ModelAttribute("client") ClientView pClientView,
                                   BindingResult pResult) {
        return postOne(-1L, pAction, pClientView, pResult);
    }

    @PostMapping("/{clientid}")
    public ModelAndView postOne(@PathVariable("clientid") Long pId,
                                @RequestParam(value = "action", required = false, defaultValue = "update") String pAction,
                                @ModelAttribute("client") ClientView pClientView,
                                BindingResult pResult) {

        this.clientViewValidator.validate(pClientView, pResult);
        ModelAndView mv = getEditViewAndDependencies(pResult);

        if (pAction.startsWith("action_")) {
            doAction(pClientView, pAction);
        } else if (pAction.equals("delete")) {
            return this.deleteOne(pId);
        } else if (!pResult.hasErrors()) {
            mv.addObject("client", pClientView);
            // Save
            pClientView = this.clientService.update(pId, pClientView);
        }

        return mv;
    }

    @DeleteMapping("/{clientid}")
    public ModelAndView deleteOne(@PathVariable("clientid") Long pId) {
        this.clientService.delete(pId);
        return index(PageRequest.of(0, 25));
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

    private ModelAndView getEditViewAndDependencies(BindingResult pResult) {
        ModelAndView mv = pResult != null && pResult.hasErrors() ? new ModelAndView(VIEW_EDIT, pResult.getModel()) : new ModelAndView(VIEW_EDIT);

        List<GrantType> grants = Arrays.asList(GrantType.values());
        List<Scope> scopes = this.scopeService.findAll();

        mv.addObject("scopes", Utils.convert(scopes, s -> new SelectableItemView(s.getId(), s.getName(), null, false)));
        mv.addObject("grants", Utils.convert(grants, g -> new SelectableItemView(null, g.toString(), null, false)));

        return mv;
    }
}
