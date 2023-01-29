package de.oth.seproject.clubhub.web.user;

import de.oth.seproject.clubhub.config.ClubUserDetails;
import de.oth.seproject.clubhub.persistence.model.Club;
import de.oth.seproject.clubhub.persistence.model.RoleType;
import de.oth.seproject.clubhub.persistence.model.User;
import de.oth.seproject.clubhub.persistence.repository.ClubRepository;
import de.oth.seproject.clubhub.persistence.repository.RoleRepository;
import de.oth.seproject.clubhub.persistence.repository.UserRepository;
import de.oth.seproject.clubhub.web.service.EmailService;
import de.oth.seproject.clubhub.web.service.NavigationService;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
public class RegistrationController {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final ClubRepository clubRepository;
    
    private final EmailService emailService;
    
    private final NavigationService navigationService;

    private final RoleRepository roleRepository;

    private final MessageSource messageSource;

    public RegistrationController(PasswordEncoder passwordEncoder, UserRepository userRepository, ClubRepository clubRepository, EmailService emailService, NavigationService navigationService, RoleRepository roleRepository, MessageSource messageSource) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.clubRepository = clubRepository;
        this.emailService = emailService;
        this.navigationService = navigationService;
        this.roleRepository = roleRepository;
        this.messageSource = messageSource;
    }

    /**
     * Adds required attributes to the model and shows the registration html page
     *
     * @param model Model for the requested html page
     * @return Name of the registration html page
     */
    @GetMapping("/registration")
    public String registrationPage(Model model) {
        User user = new User();
        model.addAttribute("user", user);

        List<Club> clubs = clubRepository.findAll();
        model.addAttribute("clubs", clubs);

        return "registration";
    }

    /**
     * Creates a new user and sends a confirmation email and shows the registration success html page if the creation was successful 
     *
     * @param user User received from the model as an attribute
     * @param result Binding result
     * @param model Model for the requested html page
     * @return Name of the registration or registration success html page
     */
    @PostMapping("/user/create")
    public String createUser(@Valid @ModelAttribute("user") final User user, BindingResult result, Model model) {
    	
    	Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
    	
    	if (optionalUser.isPresent()) {
            result.rejectValue("email", "error.email", "Es existiert bereits ein Account mit dieser E-Mail");
    		
            List<Club> clubs = clubRepository.findAll();
            model.addAttribute("clubs", clubs);
            
            return "registration";
    	}
    	
    	if (result.hasErrors()) {
            List<Club> clubs = clubRepository.findAll();
            model.addAttribute("clubs", clubs);
    		
            return "registration";
    	}

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);

        userRepository.save(user);
        
        emailService.sendEmail(user.getEmail(), "Thank you for signing up on ClubHub", "Registration was successful");

        return "registration-success";
    }

    /**
     * Adds required attributes to the model and shows the show account html page
     *
     * @param userDetails User details of the current user
     * @param model Model for the requested html page
     * @return Name of the user html page
     */
    @GetMapping("/user")
    public String accountPage(@AuthenticationPrincipal ClubUserDetails userDetails, Model model) {
        navigationService.addNavigationAttributes(model, userDetails.getUser().getId());
        boolean isTrainerInClub = roleRepository.existsByUserAndRoleName(userDetails.getUser(), RoleType.TRAINER);
        model.addAttribute("isTrainerInClub", isTrainerInClub);
        return "show-account";
    }
	
    /**
     * Adds required attributes to the model and shows the delete account confirm html page
     *
     * @param userDetails User details of the current user
     * @param model Model for the requested html page
     * @return Name of the delete account confirm html page
     */
    @GetMapping("/user/delete")
    public String deleteUser(@AuthenticationPrincipal ClubUserDetails userDetails, Model model) {
	    navigationService.addNavigationAttributes(model, userDetails.getUser().getId());
	    return "delete-account-confirm";
    }
	
    /**
     * Deletes the current user and redirects to /logout
     *
     * @param userDetails User details of the current user
     * @return Name of the redirected logout html page
     */
    @PostMapping("/user/delete")
    public String deleteUserConfirmation(@AuthenticationPrincipal ClubUserDetails userDetails) {
	    userRepository.deleteById(userDetails.getUser().getId());
	    return "redirect:/logout";
    }
	
    /**
     * Adds required attributes to the model and shows the edit account html page
     *
     * @param userDetails User details of the current user
     * @param model Model for the requested html page
     * @return Name of the edit account html page
     */
    @GetMapping("/user/edit")
    public String editUser(@AuthenticationPrincipal ClubUserDetails userDetails, Model model) {
        User user = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid announcement Id:" + userDetails.getUser().getId()));
        model.addAttribute("user", user);
		
        navigationService.addNavigationAttributes(model, user.getId());
		
        return "edit-account";
    }
	
    /**
     * Saves the new user data and adds required attributes to the model and shows the show account html page
     *
     * @param userDetails User details of the current user
     * @param user User received from the model as an attribute
     * @param result Binding result
     * @param model Model for the requested html page
     * @return Name of the show account or edit account html page
     */
    @PostMapping("/user/update")
    public String updateUser(@AuthenticationPrincipal ClubUserDetails userDetails, @Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        User persistedUser = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userDetails.getUser().getId()));
		
        if (result.hasFieldErrors("firstName") || result.hasFieldErrors("lastName") || result.hasFieldErrors("email") || result.hasFieldErrors("password")) {
            navigationService.addNavigationAttributes(model, userDetails.getUser().getId());
            return "edit-account";
        }
        
        if (!user.getEmail().equals(userDetails.getUser().getEmail())) {
            Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
	    	
            if (optionalUser.isPresent()) {
                result.rejectValue("email", "error.email", "Es existiert bereits ein Account mit dieser E-Mail");
                return "edit-account";
            }
        }
		
        persistedUser.setFirstName(user.getFirstName());
        persistedUser.setLastName(user.getLastName());
        persistedUser.setEmail(user.getEmail());
        persistedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(persistedUser);
		
        navigationService.addNavigationAttributes(model, userDetails.getUser().getId());
		
        return "show-account";
    }
}
