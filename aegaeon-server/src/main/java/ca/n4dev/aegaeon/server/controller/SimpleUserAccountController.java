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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ca.n4dev.aegaeon.server.controller.dto.ChangePasswdDto;
import ca.n4dev.aegaeon.server.controller.dto.ChangeUsernameDto;
import ca.n4dev.aegaeon.server.controller.dto.UserFormDto;
import ca.n4dev.aegaeon.server.controller.dto.UserInfoGroupDto;
import ca.n4dev.aegaeon.server.controller.validator.ChangePasswordValidator;
import ca.n4dev.aegaeon.server.controller.validator.ChangeUsernameValidator;
import ca.n4dev.aegaeon.server.security.AegaeonUserDetails;
import ca.n4dev.aegaeon.server.service.UserInfoTypeService;
import ca.n4dev.aegaeon.server.service.UserService;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.UserInfoView;
import ca.n4dev.aegaeon.server.view.UserView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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

    public static final String URL = "/user";
    public static final String URL_PROFILE = "/profile";
    public static final String URL_PASSWD = "/password";
    public static final String URL_USERNAME = "/name";
    public static final String VIEW_PROFILE = "/user/profile";
    public static final String VIEW_USERNAME = "/user/username";
    public static final String VIEW_PASSWORD = "/user/password";
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleUserAccountController.class);
    private UserService userService;
    private UserInfoTypeService userInfoTypeService;
    private ChangePasswordValidator changePasswordValidator;
    private ChangeUsernameValidator changeUsernameValidator;

    /**
     * Default Constructor.
     * @param pUserService Service to access user's informations
     * @param pUserInfoTypeService The service to get all userinfotype.
     * @param pMessages The message label source.
     */
    @Autowired
    public SimpleUserAccountController(UserService pUserService,
                                       UserInfoTypeService pUserInfoTypeService,
                                       ChangePasswordValidator pChangePasswordValidator,
                                       ChangeUsernameValidator pChangeUsernameValidator,
                                       MessageSource pMessages) {
        super(pMessages);
        userService = pUserService;
        userInfoTypeService = pUserInfoTypeService;
        changePasswordValidator = pChangePasswordValidator;
        changeUsernameValidator = pChangeUsernameValidator;
    }


    @GetMapping({"", URL_PROFILE})
    public ModelAndView getEditUser(@AuthenticationPrincipal AegaeonUserDetails pUser,
                                    Locale pLocale) {
        UserView userView = this.userService.findOne(pUser.getId());
        return editUserPage(userView);
    }

    @PostMapping(value = URL_PROFILE)
    public ModelAndView saveUser(@ModelAttribute("user") UserFormDto pDto,
                                 @AuthenticationPrincipal AegaeonUserDetails pUser,
                                 Locale pLocale) {

        UserView userView = userService.update(pUser.getId(), pDto.getUserView());
        return editUserPage(userView);
    }

    @GetMapping(URL_USERNAME)
    public ModelAndView getEditUsername(@AuthenticationPrincipal AegaeonUserDetails pUser,
                                        Locale pLocale) {
        ModelAndView mv = new ModelAndView(VIEW_USERNAME);
        mv.addObject("completed", false);
        mv.addObject("dto", new ChangeUsernameDto());

        return mv;
    }

    @PostMapping(URL_USERNAME)
    public ModelAndView updateUsername(@ModelAttribute("dto") ChangeUsernameDto pDto,
                                      @AuthenticationPrincipal AegaeonUserDetails pUser,
                                      BindingResult pResult,
                                      Locale pLocale) {

        // Basic validations
        changeUsernameValidator.validate(pDto, pResult);
        ModelAndView mv;

        if (pResult.hasErrors()) {
            mv = new ModelAndView(VIEW_USERNAME, pResult.getModel());
            mv.addObject("dto", pDto);
            mv.addObject("completed", false);
        } else {
            userService.updateUsername(pUser.getId(), pDto.getNewUsername());
            mv = new ModelAndView(VIEW_USERNAME);
            mv.addObject("completed", true);
        }


        return mv;
    }

    @GetMapping(URL_PASSWD)
    public ModelAndView getEditPassword(@AuthenticationPrincipal AegaeonUserDetails pUser,
                                        Locale pLocale) {
        ModelAndView mv = new ModelAndView(VIEW_PASSWORD);
        mv.addObject("completed", false);
        mv.addObject("dto", new ChangePasswdDto());

        return mv;
    }

    @PostMapping(URL_PASSWD)
    public ModelAndView updatePassword(@ModelAttribute("dto") ChangePasswdDto pDto,
                                       @AuthenticationPrincipal AegaeonUserDetails pUser,
                                       BindingResult pResult,
                                       Locale pLocale) {
        ModelAndView mv = null;

        this.changePasswordValidator.validate(pDto, pResult);

        if (pResult.hasErrors()) {
            mv = new ModelAndView(VIEW_PASSWORD, pResult.getModel());
            mv.addObject("dto", new ChangePasswdDto());

        } else {

            // update
            mv = new ModelAndView(VIEW_PASSWORD);
            mv.addObject("completed", true);
            userService.updatePassword(pUser.getId(), pDto.getNewPassword());
        }


        return mv;
    }

    private ModelAndView editUserPage(UserView pUserView) {
        ModelAndView editView = new ModelAndView(VIEW_PROFILE);

        // All UserInfoType
        List<UserInfoView> types = this.userInfoTypeService.findAll();

        // Split user info by tab
        Map<String, List<UserInfoView>> combineTypeValues = split(pUserView, types);

        UserFormDto dto = new UserFormDto();
        dto.setUserView(pUserView);

        editView.addObject("user", dto);
        editView.addObject("types", types);
        editView.addObject("tab", "user");
        editView.addObject("typemap", combineTypeValues);

        return editView;
    }

    private Map<String, List<UserInfoView>> split(UserView pUserView, List<UserInfoView> pUserInfoViews) {
        Map<String, List<UserInfoView>> infoViews = new LinkedHashMap<>();

        // Start by creating a Map from parent
        pUserInfoViews.forEach(pUserInfoView -> {
            if (Utils.isEmpty(pUserInfoView.getCategory())) {
                infoViews.put(pUserInfoView.getCode().toLowerCase(), new ArrayList<>());
            }
        });

        int idx = 0;

        // Add children
        for (UserInfoView pUserInfoView : pUserInfoViews) {
            if (Utils.isNotEmpty(pUserInfoView.getCategory())) {

                UserInfoView userValue = Utils.find(pUserView.getUserInfos(),
                                                    pUv -> pUv.getCode().toLowerCase().equals(pUserInfoView.getCode().toLowerCase()));

                // Add user's values
                if (userValue != null) {
                    pUserInfoView.setName(userValue.getName());
                    pUserInfoView.setValue(userValue.getValue());
                }
                pUserInfoView.setIndex(idx++);
                infoViews.get(pUserInfoView.getCategory().toLowerCase()).add(pUserInfoView);
            }

        }

        return infoViews;
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





























    /*





    // --- OLD ---
    @RequestMapping("")
    public ModelAndView account(@AuthenticationPrincipal AegaeonUserDetails pUser, Locale pLocale) {
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

    @PostMapping("")
    public ModelAndView saveAccount(@ModelAttribute("user") UserFormDto pModel, 
                                    @AuthenticationPrincipal AegaeonUserDetails pUser,
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
    */
}
