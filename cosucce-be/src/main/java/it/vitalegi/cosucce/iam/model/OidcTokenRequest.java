package it.vitalegi.cosucce.iam.model;

import lombok.Data;

@Data
public class OidcTokenRequest {
    String code;
    String redirectUrl;
}
