package org.example.whatsdownbackend.util;

import org.example.whatsdownbackend.entity.Permission;
import org.example.whatsdownbackend.entity.Role;
import org.example.whatsdownbackend.entity.User;
import org.example.whatsdownbackend.entity.GroupRole;
import org.example.whatsdownbackend.repository.PermissionRepository;
import org.example.whatsdownbackend.repository.RoleRepository;
import org.example.whatsdownbackend.repository.UserRepository;
import org.example.whatsdownbackend.repository.GroupRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.annotation.PostConstruct;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

@Configuration
public class DatabaseInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private GroupRoleRepository groupRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeDatabase() {
        createPermissions();
        createRoles();
        createGroupRoles();
        createDefaultAdminUser();
    }

    /**
     * Method to create default permissions if they don't already exist.
     */
    private void createPermissions() {
        if (permissionRepository.count() == 0) {
            // Define all necessary permissions for the chat application
            Permission sendMessage = new Permission(null, "SEND_MESSAGE");
            Permission deleteMessage = new Permission(null, "DELETE_MESSAGE");
            Permission createGroup = new Permission(null, "CREATE_GROUP");
            Permission deleteGroup = new Permission(null, "DELETE_GROUP");
            Permission addMember = new Permission(null, "ADD_MEMBER");
            Permission removeMember = new Permission(null, "REMOVE_MEMBER");
            Permission viewMessages = new Permission(null, "VIEW_MESSAGES");
            Permission markAsRead = new Permission(null, "MARK_AS_READ");
            Permission uploadAttachment = new Permission(null, "UPLOAD_ATTACHMENT");
            Permission downloadAttachment = new Permission(null, "DOWNLOAD_ATTACHMENT");
            Permission manageContacts = new Permission(null, "MANAGE_CONTACTS");
            // Add more permissions as needed

            // Save all permissions to the repository
            permissionRepository.saveAll(Arrays.asList(
                    sendMessage,
                    deleteMessage,
                    createGroup,
                    deleteGroup,
                    addMember,
                    removeMember,
                    viewMessages,
                    markAsRead,
                    uploadAttachment,
                    downloadAttachment,
                    manageContacts
            ));

            System.out.println("Default permissions created.");
        }
    }

    /**
     * Method to create default roles if they don't already exist.
     */
    private void createRoles() {
        if (roleRepository.count() == 0) {
            // Fetch permissions
            Permission sendMessage = permissionRepository.findByName("SEND_MESSAGE")
                    .orElseThrow(() -> new RuntimeException("Permission SEND_MESSAGE not found"));
            Permission deleteMessage = permissionRepository.findByName("DELETE_MESSAGE")
                    .orElseThrow(() -> new RuntimeException("Permission DELETE_MESSAGE not found"));
            Permission createGroup = permissionRepository.findByName("CREATE_GROUP")
                    .orElseThrow(() -> new RuntimeException("Permission CREATE_GROUP not found"));
            Permission deleteGroup = permissionRepository.findByName("DELETE_GROUP")
                    .orElseThrow(() -> new RuntimeException("Permission DELETE_GROUP not found"));
            Permission addMember = permissionRepository.findByName("ADD_MEMBER")
                    .orElseThrow(() -> new RuntimeException("Permission ADD_MEMBER not found"));
            Permission removeMember = permissionRepository.findByName("REMOVE_MEMBER")
                    .orElseThrow(() -> new RuntimeException("Permission REMOVE_MEMBER not found"));
            Permission viewMessages = permissionRepository.findByName("VIEW_MESSAGES")
                    .orElseThrow(() -> new RuntimeException("Permission VIEW_MESSAGES not found"));
            Permission markAsRead = permissionRepository.findByName("MARK_AS_READ")
                    .orElseThrow(() -> new RuntimeException("Permission MARK_AS_READ not found"));
            Permission uploadAttachment = permissionRepository.findByName("UPLOAD_ATTACHMENT")
                    .orElseThrow(() -> new RuntimeException("Permission UPLOAD_ATTACHMENT not found"));
            Permission downloadAttachment = permissionRepository.findByName("DOWNLOAD_ATTACHMENT")
                    .orElseThrow(() -> new RuntimeException("Permission DOWNLOAD_ATTACHMENT not found"));
            Permission manageContacts = permissionRepository.findByName("MANAGE_CONTACTS")
                    .orElseThrow(() -> new RuntimeException("Permission MANAGE_CONTACTS not found"));
            // Add more permissions as needed

            // Define roles and assign permissions
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            userRole.setPermissions(Set.of(
                    sendMessage,
                    viewMessages,
                    markAsRead,
                    uploadAttachment,
                    downloadAttachment
                    // Add default permissions for USER
            ));

            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            adminRole.setPermissions(Set.of(
                    sendMessage,
                    deleteMessage,
                    createGroup,
                    deleteGroup,
                    addMember,
                    removeMember,
                    viewMessages,
                    markAsRead,
                    uploadAttachment,
                    downloadAttachment,
                    manageContacts
                    // Add all permissions for ADMIN
            ));

            // Optionally, define more roles like ROLE_GROUP_OWNER, ROLE_MODERATOR, etc.

            roleRepository.saveAll(Arrays.asList(userRole, adminRole));

            System.out.println("Default roles with permissions created: ROLE_USER, ROLE_ADMIN");
        }
    }

    /**
     * Method to create group-specific roles if they don't already exist.
     */
    private void createGroupRoles() {
        if (groupRoleRepository.count() == 0) {
            GroupRole groupOwner = new GroupRole(null, "GROUP_OWNER");
            GroupRole groupAdmin = new GroupRole(null, "GROUP_ADMIN");
            GroupRole groupMember = new GroupRole(null, "GROUP_MEMBER");

            groupRoleRepository.saveAll(Arrays.asList(
                    groupOwner,
                    groupAdmin,
                    groupMember
            ));

            System.out.println("Group roles created: GROUP_OWNER, GROUP_ADMIN, GROUP_MEMBER");
        }
    }

    /**
     * Method to create a default admin user if not present.
     */
    private void createDefaultAdminUser() {
        Optional<User> adminUser = userRepository.findByEmail("admin@example.com");
        if (adminUser.isEmpty()) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            User user = new User();
            user.setUsername("admin");
            user.setEmail("admin@example.com");
            user.setPassword(passwordEncoder.encode("admin123")); // Use a secure password in production
            user.setRoles(Set.of(adminRole));
            user.setStatus("online");

            userRepository.save(user);

            System.out.println("Default admin user created with email 'admin@example.com' and password 'admin123'");
        }
    }
}
