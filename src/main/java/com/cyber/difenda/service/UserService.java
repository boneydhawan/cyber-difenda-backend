package com.cyber.difenda.service;

import com.cyber.difenda.model.Role;
import com.cyber.difenda.model.User;
import com.cyber.difenda.repository.RoleRepository;
import com.cyber.difenda.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /*public void setRolesInToken(String email) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));
                
        Optional<Role> role = roleRepository.findByUserId(user.getId());
        
        List<Role> roles = //Arrays.asList(role.get());        
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        
        FirebaseAuth.getInstance().setCustomUserClaims(user.getUid(), claims);

    }*/

    /*public List<String> getRolesFromToken(String idToken) throws Exception {
        return (List<String>) FirebaseAuth.getInstance()
                .verifyIdToken(idToken)
                .getClaims()
                .get("roles");
    }*/
    
    public User getUserById(Long id) throws Exception {
        return userRepository.findById(id)
                .orElseThrow(() -> new Exception("User not found"));
    }
}
