package de.oth.seproject.clubhub.web;

import de.oth.seproject.clubhub.config.ClubUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Value("${spring.application.name}")
    private String appName;

    @GetMapping("/")
    public String landingPage(@AuthenticationPrincipal ClubUserDetails userDetails, Model model) {
        model.addAttribute("appName", appName);
        return "home";
    }

    @GetMapping("/home")
    public String homePage(@AuthenticationPrincipal ClubUserDetails userDetails, Model model) {
        model.addAttribute("appName", appName);
        return "home";
    }
}
