package dev.phoenixofforce.story_game.connection;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class HeartbeatController {

    @GetMapping("ping")
    public String ping() {
        return "pong";
    }

}
