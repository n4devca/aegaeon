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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ca.n4dev.aegaeon.server.controller.validator.UserFormDtoValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ca.n4dev.aegaeon.server.controller.dto.UserFormDto;
import ca.n4dev.aegaeon.server.controller.dto.UserInfoGroupDto;
import ca.n4dev.aegaeon.server.security.SpringAuthUserDetails;
import ca.n4dev.aegaeon.server.service.UserInfoService;
import ca.n4dev.aegaeon.server.service.UserInfoTypeService;
import ca.n4dev.aegaeon.server.service.UserService;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.UserInfoView;
import ca.n4dev.aegaeon.server.view.UserView;

/**
 * SimpleUserProfileController.java
 * 
 * A simple controller managing user account.
 *
 * @author by rguillemette
 * @since Jun 12, 2017
 */
@Controller
@RequestMapping(value = SimpleUserAccountController.URL)
@ConditionalOnProperty(prefix = "aegaeon.modules", name = "account", havingValue = "true", matchIfMissing = true)
public class SimpleUserAccountController extends BaseUiController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleUserAccountController.class);
    
    public static final String URL = "/user-account";
    public static final String VIEW = "/user/user-account";
    
    private static final String CODE_SAVESTATE_NORMAL = "normal";
    private static final String CODE_SAVESTATE_MODIFIED = "modified";
    private static final String CODE_SAVESTATE_SAVED = "saved";
    
    private static final String ACTION_ADD = "add_";
    private static final String ACTION_REMOVE = "remove_";
    private static final String ACTION_SAVE = "update";
    
    private UserService userService;
    private UserInfoService userInfoService;
    private UserInfoTypeService userInfoTypeService;
    private UserFormDtoValidator userFormDtoValidator;
    
    
    /**
     * Default Constructor.
     * @param pUserService Service to access user's informations
     * @param pUserInfoService User info service.
     * @param pUserInfoTypeService The service to get all userinfotype.
     * @param pMessages The message label source.
     */
    @Autowired
    public SimpleUserAccountController(UserService pUserService, 
                                       UserInfoService pUserInfoService, 
                                       UserInfoTypeService pUserInfoTypeService, 
                                       UserFormDtoValidator pUserFormDtoValidator,
                                       MessageSource pMessages) {
        super(pMessages);
        this.userService = pUserService;
        this.userInfoService = pUserInfoService;
        this.userInfoTypeService = pUserInfoTypeService;
        this.userFormDtoValidator = pUserFormDtoValidator;
    }



    @RequestMapping("")
    public ModelAndView account(@AuthenticationPrincipal SpringAuthUserDetails pUser, Locale pLocale) {
        return createUserView(pUser.getId(), pLocale);
    }
    

    private ModelAndView createUserView(Long pUserId, Locale pLocale) {
        return createUserView(this.userService.findOne(pUserId), pLocale, null);
    }
    
    private ModelAndView createUserView(UserView pUser, Locale pLocale, BindingResult pResult) {
        ModelAndView mv = pResult != null ? new ModelAndView(VIEW, pResult.getModel()) : new ModelAndView(VIEW);
        
        // All UserInfoType
        List<UserInfoView> types = this.userInfoTypeService.findAll();
        
        // Build dto
        UserFormDto dto = new UserFormDto();
        dto.setUserView(pUser);
        dto.setGroupInfo(combine(types, pUser.getUserInfos(), pLocale));
        mv.addObject("user", dto);
        
        return mv;
    }
    
    private List<UserInfoGroupDto> combine(List<UserInfoView> pTypes, List<UserInfoView> pUserInfos, Locale pLocale) {
        
        List<UserInfoGroupDto> groups = new ArrayList<>();
        Map<String, UserInfoGroupDto> groupsMap = new HashMap<>();
        
        // Parents
        for (UserInfoView type : pTypes) {
            if (Utils.isEmpty(type.getCategory())) {
                UserInfoGroupDto cat = new UserInfoGroupDto();
                cat.setCode(type.getName());
                cat.setLabelName(getLabel("entity.userinfotype." + type.getCode(), pLocale));
                
                groupsMap.put(type.getCode(), cat);
            }
        }
        
        // Childs
        for (UserInfoView type : pTypes) {
            if (type.getCategory() != null) {
                
                UserInfoGroupDto parent = groupsMap.get(type.getCategory());

                // User Values
                UserInfoView userValue = Utils.find(pUserInfos, ui -> ui.getRefTypeId().equals(type.getRefTypeId()));
                
                if (userValue != null) {
                    parent.addUserInfoTypeDto(userValue);                    
                } else {
                    parent.addUserInfoTypeDto(type);
                }
                
            }
        }

        groups.addAll(groupsMap.values());
        
        return groups;
    }
    
    @PostMapping("")
    public ModelAndView saveAccount(@ModelAttribute("user") UserFormDto pModel, 
                                    @AuthenticationPrincipal SpringAuthUserDetails pUser,
                                    BindingResult pResult,
                                    Locale pLocale) {

        this.userFormDtoValidator.validate(pModel, pResult);

        if (pResult.hasErrors()) {
            return createUserView(pModel.getUserView(), pLocale, pResult);
        }

        UserView uv = pModel.getUserView();
        List<UserInfoView> keepUserInfos = new ArrayList<>();
        
        // Rebuild UserInfoView
        if (Utils.isNotEmpty(pModel.getGroupInfo())) {
            
            for (UserInfoGroupDto group : pModel.getGroupInfo()) {
                for (UserInfoView uiv: group.getChildren()) {
                    if (Utils.isNotEmpty(uiv.getValue())) {
                        keepUserInfos.add(uiv);
                    }
                }
            }
        }
        
        uv.setUserInfos(keepUserInfos);
        
        this.userService.update(pUser.getId(), uv);
        
        return account(pUser, pLocale);
    }

}
