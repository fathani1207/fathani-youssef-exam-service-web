package com.example.examtp.services.authentication;

import com.example.examtp.dto.authentication.LoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthenticationSeviceImp implements AuthenticationSevice{

//    @Value("${login.uri}")
    private String loginUri = "http://localhost:8081/realms/myrealm/protocol/openid-connect/token";

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.grant-type}")
    private String grantType;

    public final RestTemplate restTemplate;

    @Autowired
    public AuthenticationSeviceImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Object login(LoginDto loginDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("username", loginDto.username());
        formData.add("password", loginDto.password());

        // Create request entity
        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<>(formData, headers);

        ResponseEntity<Object> response = this.restTemplate.postForEntity(loginUri, requestEntity, Object.class);

        return response.getBody();
    }
}


class LoginRequest {
    public String username;
    public String password;
    public String client_id;
    public String grant_type;

    public LoginRequest(String username, String password, String client_id, String grant_type) {
        this.username = username;
        this.password = password;
        this.client_id = client_id;
        this.grant_type = grant_type;
    }
}
