package de.oth.seproject.clubhub.persistence.repository;

import de.oth.seproject.clubhub.persistence.model.Group;
import de.oth.seproject.clubhub.persistence.model.Role;
import de.oth.seproject.clubhub.persistence.model.RoleType;
import de.oth.seproject.clubhub.persistence.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByUserAndGroup(User user, Group group);

    Boolean existsByUserAndGroup(User user, Group group);

    Page<Role> findAllByGroup(Group group, Pageable pageable);

    Boolean existsAllByGroup(Group group);

    Boolean existsByGroupAndRoleName(Group group, RoleType roleType);

    Boolean existsByUserAndRoleName(User user, RoleType roleType);

}
