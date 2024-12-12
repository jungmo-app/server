package jungmo.server.global;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Welcome to Jungmo Server!";
    }
    @GetMapping("auth/login-success")
    public String login() {
        return "Welcome to Jungmo Server!";
    }
}
