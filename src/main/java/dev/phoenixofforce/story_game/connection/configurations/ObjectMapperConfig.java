package dev.phoenixofforce.story_game.connection.configurations;

import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class ObjectMapperConfig {

    public static final ObjectMapper MAPPER = new ObjectMapper();

}
