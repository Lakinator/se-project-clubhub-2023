package de.oth.seproject.clubhub.persistence.repository;

import de.oth.seproject.clubhub.persistence.model.Group;
import de.oth.seproject.clubhub.persistence.model.Role;
import de.oth.seproject.clubhub.persistence.model.RoleType;
import de.oth.seproject.clubhub.persistence.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * @param group -
     * @return All roles from a group
     */
    List<Role> findAllByGroup(Group group);

    /**
     * @param group    -
     * @param pageable -
     * @return A page with roles from a group
     */
    Page<Role> findAllByGroup(Group group, Pageable pageable);

    /**
     * @param user     -
     * @param roleType -
     * @return All roles from a user with a role name
     */
    List<Role> findAllByUserAndRoleName(User user, RoleType roleType);

    /**
     * @param group -
     * @return Whether the group has any roles. If false, there are no members in this group
     */
    Boolean existsAllByGroup(Group group);

    /**
     * @param user  -
     * @param group -
     * @return The current role of a user in the given group or {@link Optional#empty()} if there is no role
     */
    Optional<Role> findByUserAndGroup(User user, Group group);

    /**
     * @param user  -
     * @param group -
     * @return Whether the user has a role in this group
     */
    Boolean existsByUserAndGroup(User user, Group group);


    /**
     * @param user  -
     * @return All roles from a user
     */
    List<Role> findAllByUser(User user);

    /**
     * @param user     -
     * @param roleType -
     * @return Whether the user has this role in any group
     */
    Boolean existsByUserAndRoleName(User user, RoleType roleType);

    /**
     * @param user     -
     * @param group    -
     * @param roleType -
     * @return Whether the user has this role in a specific group
     */
    Boolean existsByUserAndGroupAndRoleName(User user, Group group, RoleType roleType);

}
