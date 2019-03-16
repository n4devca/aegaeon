package ca.n4dev.aegaeon.server.controller;

import java.time.Instant;

import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * IntrospectControllerTest.java
 * <p>
 * Testing introspect controller.
 *
 * @author rguillemette
 * @since 2.0.0 - May 03 - 2018
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class IntrospectControllerTest {

    private static final String CLIENT_PUBLIC_ID = "ca.n4dev.auth.client";
    private static final String CLIENT_PASSWD = "ca.n4dev.auth.client";

    private static final String ACT_VALID =
            "eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiJjYS5uNGRldi5hdXRoLmNsaWVudCIsImF1ZCI6ImNhLm40ZGV2LmF1dGguY2xpZW50IiwiaXNzIjoibG9jYWxob3N0IiwiZXhwIjoxODQwNzM1ODQwfQ.oHQLca1tq8HNwD51-BEBHyBBdYsaDR3iwoROesf5zMnsS5u07FRim_eRlH0Cqz9cXIggrXcBnTUxgVEYEB3ejCkVp8_wIuyhAPw1fsMDL60hxj1BdMi6SN43CWZ0CKR8wntaZeL1LGowHZiXAuPH5OENM-8SUPWJ_LoDXGbjUFaxf3ZXrraIIYbD-WyhH-MzoBNzXzAHewBdHBD41dJqJE-EhPD091rAz40h6aNdSC4zE021bdmASyqExLuFNxHGKdEGqzuLAFDkZn3yfzXNode3dv1RuZblfBeDBNzPuSra4IRDXb2OgGJ8qjXxqNj0h2kDEq_YolWvH8Revgl4JA";
    private static final String ACT_INVALID =
            "eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiJjYS5uNGRldi5hdXRoLmNsaWVudCIsImF1ZCI6ImNhLm40ZGV2LmF1dGguY2xpZW50IiwiaXNzIjoibG9jYWxob3N0IiwiZXhwIjoxNTI1Mzc2MDUzfQ.P9ftJayHRmXJpABgTbOEoK4lVJzqTTwGdYI_wXbYKVUw_lpOb7mQdJ_k783jLdpmfG-9QsQ4BkcA-DZttMeSjdo8HmHLselO4Sj9H01L_9lSSRM14gMGpT8EQXrIgtdjmLOkqbq5L42Its0BOUx1qVu2lDQjqoNNuqrQ16ehp5wyQV9HoR7LlQ4EhU6jQoj1nBPROJQ_HQKIYkaWty8Uifab7ZKv4RQgSQESQDzvN1wPz88NiQKrgxO3ttFeZeGCLf5CdlEVXwv98ayoD4h933mcAb5fDElLd13xKa545HRCXvtHmlk1qWxC1S0Bj0blUoOtiND2QJDQud00f6bK0Q";

    @Autowired
    private MockMvc mockMvc;

    @Test // ok
    public void noAuthShouldGetForbidden() throws Exception {
        this.mockMvc.perform(post(IntrospectController.URL).params(introspectParams(true)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
    }

    @Test // ok
    public void authShouldGetOk() throws Exception {

        this.mockMvc
                .perform(post(IntrospectController.URL).params(introspectParams(true))
                                                       .with(user(CLIENT_PUBLIC_ID)
                                                                     .password(CLIENT_PASSWD)
                                                                     .authorities(new SimpleGrantedAuthority(
                                                                             "ROLE_CLIENT"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test // ok
    public void shouldGetActiveIntrospectResponse() throws Exception {

        int now = Long.valueOf(Instant.now().getEpochSecond()).intValue();

        this.mockMvc
                .perform(post(IntrospectController.URL).params(introspectParams(true))
                                                       .with(user(CLIENT_PUBLIC_ID)
                                                                     .password(CLIENT_PASSWD)
                                                                     .authorities(new SimpleGrantedAuthority(
                                                                             "ROLE_CLIENT"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(new StringContains("\"active\"")))
                .andExpect(jsonPath("$.active", is(true)))
                .andExpect(jsonPath("$.exp", is(greaterThan(now))))
                .andExpect(jsonPath("$.sub", notNullValue()))
                .andExpect(jsonPath("$.scope", notNullValue()))
                .andExpect(jsonPath("$.username", notNullValue()))
                .andExpect(jsonPath("$.client_id", notNullValue()))
                .andExpect(jsonPath("$.iss", notNullValue()))
                .andReturn();

    }

    @Test // ok
    public void noClientAuthorityShouldGetForbidden() throws Exception {
        this.mockMvc.perform(post(IntrospectController.URL).params(introspectParams(true))
                                                           .with(user(CLIENT_PUBLIC_ID)
                                                                         .password(CLIENT_PASSWD)
                                                                         .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                    .andDo(print())
                    .andExpect(status().isForbidden());
    }

    @Test // ok
    public void invalidParamGetErrorResponse() throws Exception {


        this.mockMvc.perform(post(IntrospectController.URL)
                                     .with(user(CLIENT_PUBLIC_ID)
                                                   .password(CLIENT_PASSWD)
                                                   .authorities(new SimpleGrantedAuthority("ROLE_CLIENT"))))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(new StringContains("\"error\"")))
                    .andExpect(content().string(new StringContains("\"invalid_request\"")));

    }


    private MultiValueMap<String, String> introspectParams(boolean pValid) {
        MultiValueMap<String, String> p = new LinkedMultiValueMap<>();

        p.add("token", pValid ? ACT_VALID : ACT_INVALID);
        p.add("agent_of_client_id", CLIENT_PUBLIC_ID);

        return p;
    }

}
