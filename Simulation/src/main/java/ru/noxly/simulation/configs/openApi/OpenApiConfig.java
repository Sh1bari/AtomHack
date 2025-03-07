package ru.noxly.simulation.configs.openApi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Open Api Configuration
 *
 * @author Vladimir Krasnov
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Atom Hack Simulation API"
        ),
        servers = {
                @Server(
                        url = "http://localhost:8081/api",
                        description = "Local server"
                ),
                @Server(
                        url = "https://hack.noxly.ru/api",
                        description = "Main server"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
@Configuration
public class OpenApiConfig {
}
