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
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleUserAccountController.class);
    
    public static final String URL = "/user-account";
    public static final String VIEW = "user-account";
    
    private static final String CODE_SAVESTATE_NORMAL = "normal";
    private static final String CODE_SAVESTATE_MODIFIED = "modified";
    private static final String CODE_SAVESTATE_SAVED = "saved";
    
    private static final String ACTION_ADD = "add_";
    private static final String ACTION_REMOVE = "remove_";
    private static final String ACTION_SAVE = "save";
    
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
                                    @RequestParam(value = "selecttype", required = false) String[] pSelectOptions,
    								@ModelAttribute("user") UserFormDto pModel, 
                                    @AuthenticationPrincipal SpringAuthUserDetails pUser, 
                                    Locale pLocale) {
        
        if (ACTION_SAVE.equalsIgnoreCase(pAction)) {
            return save(pUser, pModel, pLocale);
        } else if (pAction.contains(ACTION_ADD)) {
            pAction = pAction.replace(ACTION_ADD, "");
            return addInfo(pUser, pAction, pSelectOptions, pModel, pLocale);
        } else if (pAction.contains(ACTION_REMOVE)) {
            pAction = pAction.replace(ACTION_REMOVE, "");
            return removeInfo(pUser, pAction, pSelectOptions, pModel, pLocale);
        }   
        
        
        
        // If unknown, return the same page
        return account(pUser, pLocale);
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
        
        // Loop on groups
        for (Entry<String, UserInfoGroupDto> ge : pDto.getGroups().entrySet()) {
            // Loop on values
            for (UserInfoDto v : ge.getValue().getValues()) {
                
                if (v != null) { // LazyList can be null
                  if (v.getRefId() != null && v.getRefId() > 0) {
                      ui = Utils.find(infos, i -> i.getId().equals(v.getRefId()));
                      
                  } else {
                      
                      UserInfoType type = Utils.find(types, t -> t.getCode().equals(v.getCode()));
                      ui = new UserInfo();
                      ui.setUser(u);
                      ui.setType(type);
                      infos.add(ui);
                  }
                  
                  if (ui != null) {
                      infoToSave.add(ui.getId());
                      ui.setValue(v.getValue());
                  }
                }
                
            }
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
    
    private ModelAndView addInfo(SpringAuthUserDetails pUser, String pAction, String[] pSelectOptions, UserFormDto pModel, Locale pLocale) {
        
        User u = this.userService.findById(pUser.getId());
        List<UserInfo> infos = this.userInfoService.findByUserId(pUser.getId());
        List<UserInfoType> types = this.userInfoTypeService.findAll();
        ModelAndView mv = createUserView(u, infos, types, pLocale, CODE_SAVESTATE_MODIFIED, pModel.getGroups());
        UserFormDto newDto = (UserFormDto) mv.getModel().get("user");
        
        String codeToAdd = null;
        // Search what need to be added
        for (String s : pSelectOptions) {
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
                    newDto.getGroups().get(t.getParent().getCode()).addValue(uiDto);
                    break;
                }
            }
        }   
        
        
        
        return mv;
    }
    
    private ModelAndView removeInfo(SpringAuthUserDetails pUser, String pAction, String[] pSelectOptions, UserFormDto pModel, Locale pLocale) {
        User u = this.userService.findById(pUser.getId());
        List<UserInfo> infos = this.userInfoService.findByUserId(pUser.getId());
        List<UserInfoType> types = this.userInfoTypeService.findAll();
        ModelAndView mv = createUserView(u, infos, types, pLocale, CODE_SAVESTATE_MODIFIED, pModel.getGroups());
        UserFormDto newDto = (UserFormDto) mv.getModel().get("user");
        
        try {
            // Convert action to refid
            Long refId = Long.parseLong(pAction);
            
            for (Entry<String, UserInfoGroupDto> en : newDto.getGroups().entrySet()) {
                Iterator<UserInfoDto> it = en.getValue().getValues().iterator();
                boolean found = false;
                
                while (it.hasNext() && !found) {
                    if (Utils.equals(refId, it.next().getRefId())) {
                        it.remove();
                        found = true;
                    }
                }
                
                if (found) {
                    break;
                }
            }
            
        } catch (Exception e) {
            LOGGER.error("Error removing user info value: " + e.getMessage(), e);
        }
        
        return mv;
    }

    private ModelAndView createUserView(Long pUserId, Locale pLocale) {
        User u = this.userService.findById(pUserId);
        List<UserInfo> infos = this.userInfoService.findByUserId(pUserId);
        List<UserInfoType> types = this.userInfoTypeService.findAll();
        return createUserView(u, infos, types, pLocale, CODE_SAVESTATE_NORMAL);
    }
    
    private ModelAndView createUserView(User pUser, List<UserInfo> pUserInfos, List<UserInfoType> pTypes, Locale pLocale, String pSaveState) {
        return createUserView(pUser, pUserInfos, pTypes, pLocale, pSaveState, null);
    }
    
    private ModelAndView createUserView(User pUser, 
                                        List<UserInfo> pUserInfos, 
                                        List<UserInfoType> pTypes, 
                                        Locale pLocale, 
                                        String pSaveState, 
                                        Map<String, UserInfoGroupDto> pUnsavedValues) {
        ModelAndView mv = new ModelAndView(VIEW);
        
        Map<Long, UserInfoGroupDto> uitGrpDtos = createUserInfoTypeGroupDto(pTypes, pLocale);
        
        UserFormDto userDto = new UserFormDto(pUser.getName(), 
                                              pUser.getPictureUrl(), 
                                              uitGrpDtos, 
                                              createUserValues(uitGrpDtos, pUserInfos, pLocale));
       

        // Re-add value not saved yet
        if (pUnsavedValues != null) {
            
            // Add
            for (Entry<String, UserInfoGroupDto> en : pUnsavedValues.entrySet()) {
                for (UserInfoDto val : en.getValue().getValues()) {
                    if (val.getRefId() < 0) {
                        userDto.getGroups().get(en.getKey()).addValue(val);
                    }
                }
            }
            
            // Remove
            for (Entry<String, UserInfoGroupDto> en : userDto.getGroups().entrySet()) {
                Iterator<UserInfoDto> it = en.getValue().getValues().iterator();
                UserInfoGroupDto g = pUnsavedValues.get(en.getKey());
                
                if (g != null) {
                
                    while (it.hasNext()) {
                        UserInfoDto d = it.next();
                        
                        if (Utils.find(g.getValues(), v -> Utils.equals(d.getRefId(), d.getRefId())) == null) {
                            it.remove();
                        }
                    }
                }
            }
        }
        
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
