package de.oth.seproject.clubhub.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.oth.seproject.clubhub.persistence.model.GroupEvent;
import de.oth.seproject.clubhub.persistence.model.GroupEventAttendance;
import de.oth.seproject.clubhub.persistence.model.User;

public interface GroupEventAttendanceRepository extends JpaRepository<GroupEventAttendance, Long> {

    Optional<GroupEventAttendance> findByUserAndGroupEvent(User user, GroupEvent groupEvent);

    List<GroupEventAttendance> findAllByGroupEvent(GroupEvent groupEvent);

}
