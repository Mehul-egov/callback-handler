package org.qwikpe.callback.handler.configration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(info = @Info(title = "Callback-handler for ABDM",
        description = "This API provides endpoints for handling callbacks from ABDM",
        version = "1.0.0",
        contact = @Contact(name = "Mehul Thummar",
                email = "xyz@gmail.com",
                url = "https://www.mehulthummar.com"),
        termsOfService = "Terms And Conditions",
        license = @License(name = "Abdm licensed",
                url = "https://sandbox.abdm.gov.in/sandbox/v3/"
        )),
        servers = {@Server(description = "Url for localhost", url = "http://localhost:9092"),
                @Server(description = "Url for development", url = "http://abdm.qwikpe.in/")})


public class SwaggerConfiguration {

}

