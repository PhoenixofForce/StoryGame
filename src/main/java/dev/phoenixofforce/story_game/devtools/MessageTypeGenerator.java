package dev.phoenixofforce.story_game.devtools;

import cz.habarta.typescript.generator.*;
import dev.phoenixofforce.story_game.connection.messages.BaseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
@Profile("dev")
public class MessageTypeGenerator {

    @EventListener(ApplicationReadyEvent.class)
    private void onStartup() {
        log.info("Generating Frontend Message Types...");

        Settings settings = new Settings();
        settings.outputKind = TypeScriptOutputKind.module;
        settings.outputFileType = TypeScriptFileType.implementationFile;
        settings.mapEnum = EnumMapping.asEnum;
        settings.jsonLibrary = JsonLibrary.jackson2;

        TypeScriptGenerator.setLogger(new Logger(Logger.Level.Error));
        new TypeScriptGenerator(settings).generateTypeScript(Input.from(BaseMessage.class), Output.to(new File("src_ui/src/common/services/messageTypes.ts")));
    }

}
