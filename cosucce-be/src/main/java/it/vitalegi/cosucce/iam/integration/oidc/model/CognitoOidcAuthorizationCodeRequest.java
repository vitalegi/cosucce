package it.vitalegi.cosucce.iam.integration.oidc.model;

import lombok.Data;

@Data
public class CognitoOidcAuthorizationCodeRequest {
    String code;
    String redirectUrl;
}
