package com.oauth.tonr.mvc;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/redirect")
public class CodeRedirectController {

    private String authCodeTonrRedirect;
    private String accessTokenUri;
    @RequestMapping("/code")
    public String code(@RequestParam("code") String code, @RequestParam("state") String state) throws Exception {
        String client_id = "tonr-with-redirect";
        String grant_type = "authorization_code";
        System.out.println(code+","+state);
        List<NameValuePair> formParams = new ArrayList<NameValuePair>();
        formParams.add(new BasicNameValuePair("code", code));
        formParams.add(new BasicNameValuePair("grant_type", grant_type));
        formParams.add(new BasicNameValuePair("redirect_uri", authCodeTonrRedirect));
        formParams.add(new BasicNameValuePair("client_id", client_id));
        formParams.add(new BasicNameValuePair("client_secret", "secret"));
        String result = post(accessTokenUri,formParams);
        System.out.println(result);
        JSONObject object = JSONObject.parseObject(result);
        String accessToken = object.getString("access_token");
        formParams.clear();
        formParams.add(new BasicNameValuePair("access_token", accessToken));
        result = post("http://localhost:8080/sparklr/photos?format=json",formParams);
        System.out.println(result);
        return "code_result";
    }
    public static String post(String url,  List<NameValuePair> formParams) throws Exception {
        CloseableHttpClient httpClient = HttpClients.custom()
                .build();
        HttpPost httpPost = new HttpPost(url);
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, "utf-8");
        httpPost.setHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
        httpPost.setHeader("Accept","application/json");
//        httpPost.setHeader("Authorization","Bearer dG9ucjpzZWNyZXQ=");
        httpPost.setEntity(entity);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        int statusCode = response.getStatusLine().getStatusCode();
        String result = new String(EntityUtils.toString(response.getEntity()).getBytes(), "UTF-8");
        if (statusCode == HttpStatus.SC_OK) {
            return  result;
        } else {
            throw new Exception(result);
        }
    }
    public static String doGet(String url, String charset) {
        CloseableHttpClient buildSSLCloseableHttpClient = null;
        try {
            buildSSLCloseableHttpClient = HttpClients.custom()
                    .build();;
//            System.setProperty("jsse.enableSNIExtension", "false");
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
            httpGet.setHeader("Accept","application/json");
            httpGet.setHeader("Authorization","Bearer dG9ucjpzZWNyZXQ=");
            HttpResponse response = buildSSLCloseableHttpClient
                    .execute(httpGet);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    String result = EntityUtils.toString(resEntity, "UTF-8");
                    return result;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (buildSSLCloseableHttpClient != null) {
                try {
                    buildSSLCloseableHttpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public String getAuthCodeTonrRedirect() {
        return authCodeTonrRedirect;
    }

    public void setAuthCodeTonrRedirect(String authCodeTonrRedirect) {
        this.authCodeTonrRedirect = authCodeTonrRedirect;
    }

    public String getAccessTokenUri() {
        return accessTokenUri;
    }

    public void setAccessTokenUri(String accessTokenUri) {
        this.accessTokenUri = accessTokenUri;
    }
}
