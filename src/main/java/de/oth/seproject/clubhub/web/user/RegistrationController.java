package de.oth.seproject.clubhub.web.user;

import de.oth.seproject.clubhub.persistence.model.Club;
import de.oth.seproject.clubhub.persistence.model.User;
import de.oth.seproject.clubhub.persistence.repository.ClubRepository;
import de.oth.seproject.clubhub.persistence.repository.UserRepository;
import de.oth.seproject.clubhub.web.service.EmailService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
public class RegistrationController {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final ClubRepository clubRepository;
    
    private final EmailService emailService;

    public RegistrationController(PasswordEncoder passwordEncoder, UserRepository userRepository, ClubRepository clubRepository, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.clubRepository = clubRepository;
        this.emailService = emailService;
    }

    @GetMapping("/registration")
    public String registrationPage(Model model) {
        User user = new User();
        model.addAttribute("user", user);

        List<Club> clubs = clubRepository.findAll();
        model.addAttribute("clubs", clubs);

        return "registration";
    }

    @PostMapping("/user/create")
    public String createUser(@ModelAttribute("user") @Valid final User user, BindingResult result, Model model) {

        // TODO: user validation

        // TODO: check if user already exists

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);

        userRepository.save(user);
        
        //emailService.sendEmail(user.getEmail(), "Thank you for signing up on ClubHub", "Registration was successful");

        return "redirect:/login"; // TODO: show successful register page
    }

}
