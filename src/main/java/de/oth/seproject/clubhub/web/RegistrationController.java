package de.oth.seproject.clubhub.web;

import de.oth.seproject.clubhub.persistence.model.User;
import de.oth.seproject.clubhub.persistence.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class RegistrationController {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public RegistrationController(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @GetMapping("/registration")
    public String registrationPage(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "registration";
    }

    @PostMapping("/registration")
    public ModelAndView registerUser(@ModelAttribute("user") @Valid final User user, final HttpServletRequest request, final Errors errors) {

        // TODO: user validation

        // TODO: check if user already exists

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);

        userRepository.save(user);

        return new ModelAndView("login"); // TODO: show successful register page
    }

}
