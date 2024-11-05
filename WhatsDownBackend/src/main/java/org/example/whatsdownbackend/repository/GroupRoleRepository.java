package org.example.whatsdownbackend.repository;

import org.example.whatsdownbackend.entity.GroupRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRoleRepository extends JpaRepository<GroupRole, Long> {
    Optional<GroupRole> findByName(String name);
}
