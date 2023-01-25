package it.vitalegi.budget.config;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import it.vitalegi.budget.exception.handler.ErrorResponse;
import lombok.extern.log4j.Log4j2;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
@Log4j2
public class OpenApiConfiguration {

    @Bean
    public OpenApiCustomiser customerGlobalHeaderOpenApiCustomiser() {
        return openApi -> {
            openApi.getPaths()
                   .values()
                   .forEach(pathItem -> pathItem.readOperations()
                                                .forEach(operation -> {
                                                    ApiResponses apiResponses = operation.getResponses();
                                                    unauthorised(apiResponses);
                                                    forbidden(apiResponses);
                                                    internalError(apiResponses);
                                                }));
        };
    }

    protected void unauthorised(ApiResponses apiResponses) {
        ApiResponse apiResponse = new ApiResponse().description("Unauthorized");
        apiResponses.addApiResponse("401", apiResponse);
    }

    protected void forbidden(ApiResponses apiResponses) {
        ApiResponse apiResponse = new ApiResponse().description("Forbidden")
                                                   .content(error());
        apiResponses.addApiResponse("403", apiResponse);
    }

    protected void internalError(ApiResponses apiResponses) {
        ApiResponse apiResponse = new ApiResponse().description("Internal server error")
                                                   .content(error());
        apiResponses.addApiResponse("500", apiResponse);
    }

    protected Content error() {
        return new Content().addMediaType(APPLICATION_JSON_VALUE, errorMediaType());
    }

    protected MediaType errorMediaType() {
        return new MediaType().schema(ModelConverters.getInstance()
                                                     .resolveAsResolvedSchema(new AnnotatedType(ErrorResponse.class)).schema);
    }


}

