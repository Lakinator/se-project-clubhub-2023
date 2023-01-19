package de.oth.seproject.clubhub.web.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    /**
     * @return Name of the login html page
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

}
