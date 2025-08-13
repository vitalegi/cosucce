package it.vitalegi.cosucce.model;

import lombok.Data;

@Data
public class OidcTokenRequest {
    String code;
    String redirectUrl;
}
