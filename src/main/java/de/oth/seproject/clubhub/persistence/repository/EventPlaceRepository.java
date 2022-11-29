package de.oth.seproject.clubhub.persistence.repository;

import de.oth.seproject.clubhub.persistence.model.EventPlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventPlaceRepository extends JpaRepository<EventPlace, Long> {
}
