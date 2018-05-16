package com.openenglish.pp.controller;

import com.google.gson.reflect.TypeToken;

import com.openenglish.pp.common.api.model.TokenDecodedInfo;
import com.openenglish.pp.service.JwtTokenService;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class JwtTokenControllerTest extends BaseControllerTest {

  @Injectable
  private JwtTokenService jwtTokenService;

  @Tested
  private JwtTokenController jwtTokenController;


  private final static String decodedTokenInfoUri = "/sso-tokens?accessToken=token";

  @Test
  public void testGetDecodedTokenOk() throws Exception {
    TokenDecodedInfo tokenDecodedInfo = new TokenDecodedInfo();
    tokenDecodedInfo.setScope(List.of("lp2-ui"))
        .setRoleIds(List.of(1L))
        .setContactId("contactId")
        .setPersonId(1L)
        .setIssuedDate(new Date())
        .setExpiredDate(new Date());
    MockMvc mockMvc = ControllerTestUtil.buildMockMvc(jwtTokenController);
    new Expectations() {{
      jwtTokenService.getTokenDecodedInfo(anyString);
      result = tokenDecodedInfo;
    }};

    ResultActions perform = mockMvc.perform(get(decodedTokenInfoUri));

    //verifying the response
    MvcResult mvcResult = perform.andReturn();
    MockHttpServletResponse response = mvcResult.getResponse();
    assertThat(response.getStatus(), is(HttpStatus.OK.value()));
    assertThat(response.getContentType(), is(MediaType.APPLICATION_JSON_UTF8.toString()));
    TokenDecodedInfo result =
        gson.fromJson(response.getContentAsString(), TokenDecodedInfo.class);
    assertThat(result.getPersonId(), is(tokenDecodedInfo.getPersonId()));
    assertThat(result.getContactId(), is(tokenDecodedInfo.getContactId()));
    assertThat(result.getIssuedDate(), is(tokenDecodedInfo.getIssuedDate()));
    assertThat(result.getExpiredDate(), is(tokenDecodedInfo.getExpiredDate()));
    assertThat(result.getScope(), is(tokenDecodedInfo.getScope()));
    assertThat(result.getRoleIds(), is(tokenDecodedInfo.getRoleIds()));
  }


  @Test
  public void testGetDecodedTokenValidationError() throws Exception {
    MockMvc mockMvc = ControllerTestUtil.buildMockMvc(jwtTokenController);
    new Expectations() {{
      jwtTokenService.getTokenDecodedInfo(anyString);
      result = new IllegalArgumentException("Token is empty!");
    }};

    ResultActions perform = mockMvc.perform(get(decodedTokenInfoUri));

    //verifying the response
    MvcResult mvcResult = perform.andReturn();
    MockHttpServletResponse response = mvcResult.getResponse();
    assertThat(response.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
    assertThat(response.getContentType(), is(MediaType.APPLICATION_JSON_UTF8.toString()));
    Type type = new TypeToken<Map<String, String>>() {
    }.getType();
    Map<String, String> map = gson.fromJson(response.getContentAsString(), type);

    assertNotNull(map.get("errorCode"));
    assertThat(map.get("errorMessage"), is("Token is empty!"));
  }


  @Test
  public void testGetDecodedTokenError() throws Exception {
    MockMvc mockMvc = ControllerTestUtil.buildMockMvc(jwtTokenController);
    new Expectations() {{
      jwtTokenService.getTokenDecodedInfo(anyString);
      result = new Exception("Error");
    }};

    ResultActions perform = mockMvc.perform(get(decodedTokenInfoUri));

    //verifying the response
    MvcResult mvcResult = perform.andReturn();
    MockHttpServletResponse response = mvcResult.getResponse();
    assertThat(response.getStatus(), is(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    assertThat(response.getContentType(), is(MediaType.APPLICATION_JSON_UTF8.toString()));
    Type type = new TypeToken<Map<String, String>>() {
    }.getType();
    Map<String, String> map = gson.fromJson(response.getContentAsString(), type);

    assertNotNull(map.get("errorCode"));
    assertThat(map.get("errorMessage"), is("Error"));
  }
}
