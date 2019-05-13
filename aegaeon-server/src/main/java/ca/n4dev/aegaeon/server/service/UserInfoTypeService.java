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

package ca.n4dev.aegaeon.server.service;

import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.api.model.UserInfoType;
import ca.n4dev.aegaeon.api.repository.UserInfoTypeRepository;
import ca.n4dev.aegaeon.server.utils.Assert;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.UserInfoView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author by rguillemette
 * @since Sep 4, 2017
 */
@Service
public class UserInfoTypeService extends BaseSecuredService<UserInfoType, UserInfoTypeRepository> {

    private Comparator<UserInfoView> viewComparator = Comparator.comparing(UserInfoView::getCode);

    /**
     * @param pRepository
     */
    @Autowired
    public UserInfoTypeService(UserInfoTypeRepository pRepository) {
        super(pRepository);
    }

    /**
     * Find all user info type.
     *
     * @return All entities.
     */
    @Transactional(readOnly = true)
    public List<UserInfoView> findAll() {
        List<UserInfoType> uit = getRepository().findAll();
        List<UserInfoView> userInfoViews = Utils.convert(uit, t -> {
            UserInfoView userInfoTypeView = new UserInfoView();

            userInfoTypeView.setCode(t.getCode());
            userInfoTypeView.setRefTypeId(t.getId());

            return userInfoTypeView;
        });

        Collections.sort(userInfoViews, viewComparator);

        return userInfoViews;
    }


    List<UserInfoType> findAllType() {
        List<UserInfoType> uit = getRepository().findAll();
        return uit;
    }

    @Transactional(readOnly = true)
    public UserInfoView findByCode(String pCode) {
        Assert.notEmpty(pCode, ServerExceptionCode.ILLEGAL_ARGUMENT);
        final UserInfoType infoType = getRepository().findByCode(pCode);

        UserInfoView userInfoView = new UserInfoView();

        userInfoView.setRefTypeId(infoType.getId());
        userInfoView.setCode(pCode);

        return userInfoView;
    }
}
