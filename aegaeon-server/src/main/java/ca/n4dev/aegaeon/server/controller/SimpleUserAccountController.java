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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
    public static final String VIEW = "user-account";
    
    private static final String CODE_SAVESTATE_NORMAL = "normal";
    private static final String CODE_SAVESTATE_MODIFIED = "modified";
    private static final String CODE_SAVESTATE_SAVED = "saved";
    
    private static final String ACTION_ADD = "add_";
    private static final String ACTION_REMOVE = "remove_";

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
        return createUserView(pUser.getId(), pLocale);
    }
    
    @PostMapping("")
    public ModelAndView changeOrSaveAccount(@RequestParam("action") String pAction,
                                    @RequestParam(value = "selecttype", required = false) String[] pUserType,
    								@ModelAttribute("user") UserFormDto pModel, 
                                    @AuthenticationPrincipal SpringAuthUserDetails pUser, 
                                    Locale pLocale) {
        
        if ("save".equalsIgnoreCase(pAction)) {
            return save(pUser, pModel, pLocale);
        } else {
            return addOrRemoveInfo(pAction, pUserType, pModel, pLocale);
        }
    }

    private ModelAndView addOrRemoveInfo(String pAction, String[] pUserType, UserFormDto pModel, Locale pLocale) {
        ModelAndView mv = new ModelAndView(VIEW);
        mv.addObject("saveState", getLabel("page.useraccount.form.savestate." + CODE_SAVESTATE_MODIFIED, pLocale));
        
        // Add other from the model
        
        List<UserInfoType> types = this.userInfoTypeService.findAll();
        Map<Long, UserInfoGroupDto> uitGrpDtos = createUserInfoTypeGroupDto(types, pLocale);
        
        UserFormDto newDto = new UserFormDto(pModel.getName(), pModel.getPictureUrl(), uitGrpDtos, new ArrayList<>());
        newDto.setUserValues(pModel.getUserValues());
        
        // Selection ?
        String codeToAdd = null;
        
        if (pAction.contains(ACTION_ADD)) {
            pAction = pAction.replace(ACTION_ADD, "");
            for (String s : pUserType) {
                if (s.startsWith(pAction)) {
                    codeToAdd = s.replace(pAction + "-", "");
                    break;
                }
            }
            
            if (Utils.isNotEmpty(codeToAdd)) {
                
                for (UserInfoType t : types) {
                    if (t.getCode().equalsIgnoreCase(codeToAdd)) {
                        UserInfoDto uiDto = new UserInfoDto(Utils.nextNegativeId(),
                                t.getCode(), 
                                getLabel("entity.userinfotype." + t.getCode(), pLocale));
                        uiDto.setParentCode(t.getParent().getCode());
                        newDto.addUserValue(uiDto);
                        break;
                    }
                }
            }            
        } else if (pAction.contains(ACTION_REMOVE)) {
            pAction = pAction.replace(ACTION_REMOVE, "");
            
            if (Utils.isNotEmpty(pAction)) {
                
                Iterator<UserInfoDto> it = newDto.getUserValues().iterator();
                boolean found = false;
                UserInfoDto dto = null;
                while (it.hasNext() && !found) {
                    dto = it.next();
                    
                    if (pAction.equals(Utils.asString(dto.getRefId()))) {
                        it.remove();
                        found = true;
                    }
                }
            }
        }
        
        mv.addObject("user", newDto);
        
        return mv;
    }
    
    private ModelAndView save(SpringAuthUserDetails pUser, UserFormDto pDto, Locale pLocale) {
        
        User u = this.userService.findById(pUser.getId());
        List<UserInfo> infos = this.userInfoService.findByUserId(pUser.getId());
        List<UserInfoType> types = this.userInfoTypeService.findAll();
        UserInfo ui;
        
        u.setName(pDto.getName());
        u.setPictureUrl(pDto.getPictureUrl());
        
        // Save user
        this.userService.save(u);
        
        // user info
        List<Long> infoToSave = new ArrayList<>();
        
        // Set
        for (UserInfoDto v : pDto.getUserValues()) {
            if (v.getRefId() != null) {
                ui = Utils.find(infos, i -> i.getId().equals(v.getRefId()));
                infoToSave.add(ui.getId());
            } else {
                
                UserInfoType type = Utils.find(types, t -> t.getCode().equals(v.getParentCode()));
                ui = new UserInfo();
                ui.setUser(u);
                ui.setType(type);
                infos.add(ui);
            }
            
            ui.setValue(v.getValue());
            
        }
        
        // Delete
        Iterator<UserInfo> it = infos.iterator();
        UserInfo i = null;
        while (it.hasNext()) {
            i = it.next();
            if (i.getId() != null && !infoToSave.contains(i.getId())) {
                this.userInfoService.delete(i);
                it.remove();
            }
        }
        
        // Save
        this.userInfoService.save(infos);
        
        return createUserView(u, infos, types, pLocale, CODE_SAVESTATE_SAVED);
    }
    
    private ModelAndView createUserView(Long pUserId, Locale pLocale) {
        User u = this.userService.findById(pUserId);
        List<UserInfo> infos = this.userInfoService.findByUserId(pUserId);
        List<UserInfoType> types = this.userInfoTypeService.findAll();
        return createUserView(u, infos, types, pLocale, CODE_SAVESTATE_NORMAL);
    }
    
    private ModelAndView createUserView(User u, List<UserInfo> pUserInfos, List<UserInfoType> pTypes, Locale pLocale, String pSaveState) {
        ModelAndView mv = new ModelAndView(VIEW);
        
        Map<Long, UserInfoGroupDto> uitGrpDtos = createUserInfoTypeGroupDto(pTypes, pLocale);
        
        UserFormDto userDto = new UserFormDto(u.getName(), 
                                              u.getPictureUrl(), 
                                              uitGrpDtos, 
                                              createUserValues(uitGrpDtos, pUserInfos, pLocale));
       
        mv.addObject("saveState", getLabel("page.useraccount.form.savestate." + pSaveState, pLocale));
        mv.addObject("user", userDto);
        
        return mv;
    }
    
    private List<UserInfoDto> createUserValues(Map<Long, UserInfoGroupDto> pAllUserInfoGroupDto, List<UserInfo> pUserInfos, Locale pLocale) {
        
        List<UserInfoDto> userValues = new ArrayList<>();
        
        if (pUserInfos != null) {
            for (UserInfo ui : pUserInfos) {
                UserInfoType type = ui.getType();
                String userInfoTypeCode = type.getCode();
                Long typeId = Utils.coalesce(type.getParentId(), type.getId());

                UserInfoGroupDto g = pAllUserInfoGroupDto.get(typeId);
                
                UserInfoDto uiDto = new UserInfoDto(ui.getId(),
                                                    userInfoTypeCode, 
                                                    Utils.coalesce(ui.getOtherName(), 
                                                                   getLabel("entity.userinfotype." + userInfoTypeCode, pLocale)),
                                                                   ui.getValue());
                uiDto.setParentCode(g.getCode());
                
                userValues.add(uiDto);
            }            
        }
        
        return userValues;
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
                        new UserInfoDto(uit.getId(), uit.getCode(), getLabel("entity.userinfotype." + uit.getCode(), pLocale)));
            }
        }
        
        return groups;
    }
}
