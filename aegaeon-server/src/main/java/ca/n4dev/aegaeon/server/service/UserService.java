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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import ca.n4dev.aegaeon.api.exception.OpenIdException;
import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.api.logging.OpenIdEvent;
import ca.n4dev.aegaeon.api.logging.OpenIdEventLogger;
import ca.n4dev.aegaeon.api.model.Authority;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.model.UserInfo;
import ca.n4dev.aegaeon.api.model.UserInfoType;
import ca.n4dev.aegaeon.api.repository.AuthorityRepository;
import ca.n4dev.aegaeon.api.repository.UserInfoRepository;
import ca.n4dev.aegaeon.api.repository.UserRepository;
import ca.n4dev.aegaeon.api.token.OAuthClient;
import ca.n4dev.aegaeon.api.token.OAuthUser;
import ca.n4dev.aegaeon.api.token.payload.Claims;
import ca.n4dev.aegaeon.api.token.payload.PayloadProvider;
import ca.n4dev.aegaeon.api.validation.PasswordEvaluator;
import ca.n4dev.aegaeon.server.security.AccessTokenAuthentication;
import ca.n4dev.aegaeon.server.utils.Assert;
import ca.n4dev.aegaeon.server.utils.Differentiation;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.UserInfoResponseView;
import ca.n4dev.aegaeon.server.view.UserInfoView;
import ca.n4dev.aegaeon.server.view.UserView;
import ca.n4dev.aegaeon.server.view.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserService.java
 * <p>
 * User service.
 *
 * @author by rguillemette
 * @since May 8, 2017
 */
@Service
public class UserService extends BaseSecuredService<User, UserRepository> implements PayloadProvider {

    private OpenIdEventLogger openIdEventLogger;
    private UserMapper userMapper;
    private UserInfoRepository userInfoRepository;
    private AuthorityRepository authorityRepository;
    private UserInfoTypeService userInfoTypeService;
    private PasswordEncoder passwordEncoder;
    private PasswordEvaluator passwordEvaluator;

    /**
     * Default constructor.
     *
     * @param pRepository The user repo.
     */
    @Autowired
    public UserService(UserRepository pRepository,
                       AuthorityRepository pAuthorityRepository,
                       UserInfoRepository pUserInfoRepository,
                       UserInfoTypeService pUserInfoTypeService,
                       PasswordEncoder pPasswordEncoder,
                       OpenIdEventLogger pOpenIdEventLogger,
                       UserMapper pUserMapper,
                       PasswordEvaluator pPasswordEvaluator) {
        super(pRepository);
        this.authorityRepository = pAuthorityRepository;
        this.userInfoRepository = pUserInfoRepository;
        this.passwordEncoder = pPasswordEncoder;
        this.openIdEventLogger = pOpenIdEventLogger;
        this.userMapper = pUserMapper;
        this.userInfoTypeService = pUserInfoTypeService;
        this.passwordEvaluator = pPasswordEvaluator;
    }

    /**
     * @param pUserId
     * @return
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('CLIENT') or principal.id == #pUserId")
    public UserView findOne(Long pUserId) {
        User user = super.findById(pUserId);
        List<UserInfo> ui = this.userInfoRepository.findByUserId(pUserId);

        return this.userMapper.toView(user, ui);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('CLIENT') or principal == #pAccessTokenAuthentication.accessToken")
    public UserInfoResponseView info(AccessTokenAuthentication pAccessTokenAuthentication) {
        Assert.notNull(pAccessTokenAuthentication, ServerExceptionCode.USER_EMPTY);

        UserView u = findOne(pAccessTokenAuthentication.getUserId());
        Assert.notNull(u, ServerExceptionCode.USER_EMPTY);

        final Set<String> scopeStrings = Utils.convert(pAccessTokenAuthentication.getScopes(), pScopeView -> pScopeView.getName());
        Map<String, String> payload = createPayload(u, null, scopeStrings);

        UserInfoResponseView response = new UserInfoResponseView(pAccessTokenAuthentication.getUniqueIdentifier(), payload);

        openIdEventLogger.log(OpenIdEvent.REQUEST_INFO, getClass(), u.getUserName(), null);

        return response;
    }

    /**
     * Create a user.
     *
     * @param pUsername  The username
     * @param pName      It's name.
     * @param pRawPasswd The choosen password
     * @return The new user.
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or isAnonymous()")
    public UserView create(String pUsername,
                           String pName,
                           String pRawPasswd) {

        Assert.notEmpty(pUsername, ServerExceptionCode.INVALID_PARAMETER);
        Assert.notEmpty(pRawPasswd, ServerExceptionCode.USER_INVALID_PASSWORD);
        Assert.isTrue(this.passwordEvaluator.evaluate(pRawPasswd).isValid(), ServerExceptionCode.USER_INVALID_PASSWORD);

        //StringRandomizer.getInstance().getRandomString(128)
        User u = new User(UUID.randomUUID().toString());
        u.setUserName(pUsername);
        u.setName(pName);
        u.setEnabled(true);
        u.setPasswd(this.passwordEncoder.encode(pRawPasswd));

        Authority authUser = this.authorityRepository.findByCode("ROLE_USER");
        u.addAuthorities(authUser);

        // Save
        u = this.save(u);

        return this.userMapper.toView(u, null);
    }


    @Transactional
    @PreAuthorize("hasRole('ADMIN') or principal.id == #pUserId")
    public UserView update(Long pUserId, UserView pUserView) {

        // Get the user and its infos
        final User u = this.findById(pUserId);
        Assert.notNull(u, ServerExceptionCode.USER_EMPTY);
        List<UserInfo> uis = this.userInfoRepository.findByUserId(pUserId);
        List<UserInfoType> allTypes = this.userInfoTypeService.findAllType();

        // Update basic info
        u.setName(pUserView.getName());
        u.setPictureUrl(pUserView.getPicture());

        // Filter null entry and value
        List<UserInfoView> userInfoViews = pUserView.getUserInfos()
                                                    .stream()
                                                    .filter(pUserInfoView -> pUserInfoView != null && Utils
                                                            .isNotEmpty(pUserInfoView.getValue()))
                                                    .collect(Collectors.toList());

        final Differentiation<UserInfo> differentiation = Utils.differentiate(uis, userInfoViews,
                                                                              (pUserInfo, pUserInfoView) ->
                                                                                      pUserInfo.getId().equals(pUserInfoView.getRefId()),
                                                                              (pUserInfo, pUserInfoView) -> {
                                                                                  pUserInfo.setDescription(pUserInfoView.getName());
                                                                                  pUserInfo.setValue(pUserInfoView.getValue());
                                                                                  return pUserInfo;
                                                                              }, pUserInfoView -> {
                    // Validate type
                    final Optional<UserInfoType> infoType = findType(allTypes, pUserInfoView);
                    final UserInfoType type = infoType.get();
                    UserInfo ui = new UserInfo(u, type, pUserInfoView.getValue());
                    ui.setDescription(Utils.coalesce(pUserInfoView.getName(), type.getCode()));

                    return ui;
                });


        // Save User
        User savedUser = this.save(u);

        List<UserInfo> userInfos = new ArrayList<>();

        // Save Info
        if (Utils.isNotEmpty(differentiation.getNewObjs())) {
            userInfos.addAll(this.userInfoRepository.saveAll(differentiation.getNewObjs()));
        }

        if (Utils.isNotEmpty(differentiation.getUpdatedObjs())) {
            userInfos.addAll(this.userInfoRepository.saveAll(differentiation.getUpdatedObjs()));
        }

        // Delete Info
        if (Utils.isNotEmpty(differentiation.getRemovedObjs())) {
            this.userInfoRepository.deleteAll(differentiation.getRemovedObjs());
        }

        userInfos = this.userInfoRepository.findByUserId(savedUser.getId());
        return this.userMapper.toView(savedUser, userInfos);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or principal.id == #pUserId")
    public void updatePassword(Long pUserId, String pRawPasswd) {
        Assert.isTrue(this.passwordEvaluator.evaluate(pRawPasswd).isValid(), ServerExceptionCode.USER_INVALID_PASSWORD);

        User u = this.findById(pUserId);

        if (u != null) {
            u.setPasswd(passwordEncoder.encode(pRawPasswd));
            this.save(u);
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or principal.id == #pUserId")
    public void updateUsername(Long pUserId, String pNewUsername) {
        User u = findById(pUserId);

        if (u != null) {

            // Check to make sure this username is not already used
            if (existsByUserName(pNewUsername)) {
                throw new OpenIdException(ServerExceptionCode.USER_INVALID_USERNAME);
            }

            u.setUserName(pNewUsername);
            save(u);
        }
    }

    @Transactional(readOnly = true)
    // @PreAuthorize("hasRole('USER') or isAnonymous()")
    public boolean existsByUserName(String pUsername) {
        return getRepository().existsByUserName(pUsername);
    }
    
    /*
     *  sub  string  Subject - Identifier for the End-User at the Issuer.
        name    string  End-User's full name in displayable form including all name parts, possibly including titles and suffixes,
        ordered according to the End-User's locale and preferences.
        given_name  string  Given name(s) or first name(s) of the End-User. Note that in some cultures, people can have multiple given
        names; all can be present, with the names being separated by space characters.
        family_name     string  Surname(s) or last name(s) of the End-User. Note that in some cultures, people can have multiple family
        names or no family name; all can be present, with the names being separated by space characters.
        middle_name     string  Middle name(s) of the End-User. Note that in some cultures, people can have multiple middle names; all
        can be present, with the names being separated by space characters. Also note that in some cultures, middle names are not used.
        nickname    string  Casual name of the End-User that may or may not be the same as the given_name. For instance, a nickname value
         of Mike might be returned alongside a given_name value of Michael.
        preferred_username  string  Shorthand name by which the End-User wishes to be referred to at the RP, such as janedoe or j.doe.
        This value MAY be any valid JSON string including special characters such as @, /, or whitespace. The RP MUST NOT rely upon this
        value being unique, as discussed in Section 5.7.
        profile     string  URL of the End-User's profile page. The contents of this Web page SHOULD be about the End-User.
        picture     string  URL of the End-User's profile picture. This URL MUST refer to an image file (for example, a PNG, JPEG, or GIF
         image file), rather than to a Web page containing an image. Note that this URL SHOULD specifically reference a profile photo of
         the End-User suitable for displaying when describing the End-User, rather than an arbitrary photo taken by the End-User.
        website     string  URL of the End-User's Web page or blog. This Web page SHOULD contain information published by the End-User or
         an organization that the End-User is affiliated with.
        email   string  End-User's preferred e-mail address. Its value MUST conform to the RFC 5322 [RFC5322] addr-spec syntax. The RP
        MUST NOT rely upon this value being unique, as discussed in Section 5.7.
        email_verified  boolean     True if the End-User's e-mail address has been verified; otherwise false. When this Claim Value is
        true, this means that the OP took affirmative steps to ensure that this e-mail address was controlled by the End-User at the time
         the verification was performed. The means by which an e-mail address is verified is context-specific, and dependent upon the
         trust framework or contractual agreements within which the parties are operating.
        gender  string  End-User's gender. Values defined by this specification are female and male. Other values MAY be used when
        neither of the defined values are applicable.
        birthdate   string  End-User's birthday, represented as an ISO 8601:2004 [ISO8601‑2004] YYYY-MM-DD format. The year MAY be 0000,
        indicating that it is omitted. To represent only the year, YYYY format is allowed. Note that depending on the underlying
        platform's date related function, providing just year can result in varying month and day, so the implementers need to take this
        factor into account to correctly process the dates.
        zoneinfo    string  String from zoneinfo [zoneinfo] time zone database representing the End-User's time zone. For example,
        Europe/Paris or America/Los_Angeles.
        locale  string  End-User's locale, represented as a BCP47 [RFC5646] language tag. This is typically an ISO 639-1 Alpha-2
        [ISO639‑1] language code in lowercase and an ISO 3166-1 Alpha-2 [ISO3166‑1] country code in uppercase, separated by a dash. For
        example, en-US or fr-CA. As a compatibility note, some implementations have used an underscore as the separator rather than a
        dash, for example, en_US; Relying Parties MAY choose to accept this locale syntax as well.
        phone_number    string  End-User's preferred telephone number. E.164 [E.164] is RECOMMENDED as the format of this Claim, for
        example, +1 (425) 555-1212 or +56 (2) 687 2400. If the phone number contains an extension, it is RECOMMENDED that the extension
        be represented using the RFC 3966 [RFC3966] extension syntax, for example, +1 (604) 555-1234;ext=5678.
        phone_number_verified   boolean     True if the End-User's phone number has been verified; otherwise false. When this Claim Value
         is true, this means that the OP took affirmative steps to ensure that this phone number was controlled by the End-User at the
         time the verification was performed. The means by which a phone number is verified is context-specific, and dependent upon the
         trust framework or contractual agreements within which the parties are operating. When true, the phone_number Claim MUST be in E
         .164 format and any extensions MUST be represented in RFC 3966 format.
        address     JSON object     End-User's preferred postal address. The value of the address member is a JSON [RFC4627] structure
        containing some or all of the members defined in Section 5.1.1.
        updated_at  number

     * */

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.payload.PayloadProvider
     * #createPayload(ca.n4dev.aegaeon.api.token.OAuthUser, ca.n4dev.aegaeon.api.token.OAuthClient, java.util.List)
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('CLIENT') or principal.id == #pOAuthUser.id")
    public Map<String, String> createPayload(OAuthUser pOAuthUser, OAuthClient pOAuthClient, Set<String> pRequestedScopes) {
        final UserView userView = findOne(pOAuthUser.getId());
        return createPayload(userView, pOAuthClient, pRequestedScopes);
    }

    private Map<String, String> createPayload(UserView pUserView, OAuthClient pOAuthClient, Set<String> pRequestedScopes) {
        Map<String, String> payload = new LinkedHashMap<>();

        final boolean hasProfile = Utils.safeSet(pRequestedScopes).contains("profile");

        if (hasProfile) {
            payload.put(Claims.NAME, pUserView.getName());
            payload.put(Claims.USERNAME, pUserView.getUserName());
        }

        return payload;
    }

    private Optional<UserInfoType> findType(List<UserInfoType> pAllType, UserInfoView pUserInfoView) {

        Assert.notNull(pAllType);
        Assert.notNull(pUserInfoView);

        final Optional<UserInfoType> userInfoTypeOptional = pAllType.stream()
                                                                    .filter(pUt -> pUt.getId().equals(pUserInfoView.getRefTypeId()) ||
                                                                            pUt.getCode().equals(pUserInfoView.getCode()))
                                                                    .findFirst();

        return userInfoTypeOptional;
    }
}
