package de.oth.seproject.clubhub.rest.v1.api.controller;

import de.oth.seproject.clubhub.rest.v1.api.dto.ClubDTO;
import de.oth.seproject.clubhub.rest.v1.api.service.ClubRestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clubs")
public non-sealed class ClubRestControllerImpl implements ClubRestController {

    private final ClubRestService clubRestService;

    public ClubRestControllerImpl(ClubRestService clubRestService) {
        this.clubRestService = clubRestService;
    }

    /**
     * {@inheritDoc}
     */
    @GetMapping("")
    @Override
    public List<ClubDTO> getAllClubs() {
        return clubRestService.getAllClubs().stream().map(ClubDTO::of).toList();
    }
}
