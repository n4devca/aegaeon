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

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.model.UserInfo;
import ca.n4dev.aegaeon.api.model.UserInfoType;
import ca.n4dev.aegaeon.server.controller.dto.UserFormDto;
import ca.n4dev.aegaeon.server.controller.dto.UserInfoDto;
import ca.n4dev.aegaeon.server.controller.dto.UserInfoGroupDto;
import ca.n4dev.aegaeon.server.security.SpringAuthUserDetails;
import ca.n4dev.aegaeon.server.service.UserInfoService;
import ca.n4dev.aegaeon.server.service.UserInfoTypeService;
import ca.n4dev.aegaeon.server.service.UserService;
import ca.n4dev.aegaeon.server.utils.Utils;

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
    
    public static final String URL = "/user-account";

    private UserService userService;
    private UserInfoService userInfoService;
    private UserInfoTypeService userInfoTypeService;
    
    
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
                                       MessageSource pMessages) {
        super(pMessages);
        this.userService = pUserService;
        this.userInfoService = pUserInfoService;
        this.userInfoTypeService = pUserInfoTypeService;
    }
    
    @RequestMapping("")
    public ModelAndView account(@AuthenticationPrincipal SpringAuthUserDetails pUser, Locale pLocale) {
        ModelAndView mv = new ModelAndView("user-account");
        
        // user
        User u = this.userService.findById(pUser.getId());
        
        // User Info
        List<UserInfo> infos = this.userInfoService.findByUserId(pUser.getId());
        
        // UserInfoType
        List<UserInfoType> types = this.userInfoTypeService.findAll();
        Map<Long, UserInfoGroupDto> uitGrpDtos = createUserInfoTypeGroupDto(types, pLocale);
        
        UserFormDto userDto = new UserFormDto();
        userDto.setName(u.getName());
        userDto.setPictureUrl(u.getPictureUrl());
        userDto.setInfos(combine(uitGrpDtos, infos, pLocale));
        
        mv.addObject("userinfos", infos);
        mv.addObject("user", userDto);
        
        return mv;
    }
    
    @PostMapping("")
    public ModelAndView saveAccount(@RequestParam("action") String pAction,
    								@ModelAttribute("user") UserInfoGroupDto pModel, 
                                    @AuthenticationPrincipal SpringAuthUserDetails pUser) {
        
        return new ModelAndView("user-account");
    }
    
    private List<UserInfoGroupDto> combine(Map<Long, UserInfoGroupDto> pAllUserInfoGroupDto, List<UserInfo> pUserInfos, Locale pLocale) {
        
        if (pUserInfos != null) {
            for (UserInfo ui : pUserInfos) {
                UserInfoType type = ui.getType();
                Long typeId = Utils.coalesce(type.getParentId(), type.getId());
                UserInfoGroupDto g = pAllUserInfoGroupDto.get(typeId);
                String userInfoTypeCode = type.getCode();
                
                UserInfoDto uiDto = new UserInfoDto(userInfoTypeCode, 
                                                    Utils.coalesce(ui.getOtherName(), 
                                                                   getLabel("entity.userinfotype." + userInfoTypeCode, pLocale)),
                                                                   ui.getValue());
                uiDto.setRefId(ui.getId());
                
                g.addUserValue(uiDto);
            }            
        }
        
        return new ArrayList<>(pAllUserInfoGroupDto.values());
    }
    
    private Map<Long, UserInfoGroupDto> createUserInfoTypeGroupDto(List<UserInfoType> pUserInfoType, Locale pLocale) {
        
        
        Map<Long, UserInfoGroupDto> groups = new HashMap<>();
        
        for (UserInfoType uit : pUserInfoType) {
            
            // Child or parent
            Long groupParentId = Utils.coalesce(uit.getParentId(), uit.getId());
            
            // Make sure parent exists
            if (!groups.containsKey(groupParentId)) {
                groups.put(groupParentId, new UserInfoGroupDto());
            }
            
            // Set info in parent
            if (uit.getParentId() == null) {
                groups.get(uit.getId()).setCode(uit.getCode());
                groups.get(uit.getId()).setLabelName(getLabel("entity.userinfotype." + uit.getCode(), pLocale));
            } else {
                groups.get(uit.getParentId()).addUserInfoTypeDto(
                        new UserInfoDto(uit.getCode(), getLabel("entity.userinfotype." + uit.getCode(), pLocale)));
            }
        }
        
        return groups;
    }
}
