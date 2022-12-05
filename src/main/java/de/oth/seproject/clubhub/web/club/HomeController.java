package de.oth.seproject.clubhub.web.club;

import de.oth.seproject.clubhub.config.ClubUserDetails;
import de.oth.seproject.clubhub.web.service.NavigationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Value("${spring.application.name}")
    private String appName;

    private final NavigationService navigationService;

    public HomeController(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @GetMapping("/")
    public String landingPage(@AuthenticationPrincipal ClubUserDetails userDetails, Model model) {
        model.addAttribute("appName", appName);
        if (userDetails != null) {
            navigationService.addNavigationAttributes(model, userDetails.getUser().getId());
        }
        return "home";
    }

    @GetMapping("/home")
    public String homePage(@AuthenticationPrincipal ClubUserDetails userDetails, Model model) {
        model.addAttribute("appName", appName);
        if (userDetails != null) {
            navigationService.addNavigationAttributes(model, userDetails.getUser().getId());
        }
        return "home";
    }
}
