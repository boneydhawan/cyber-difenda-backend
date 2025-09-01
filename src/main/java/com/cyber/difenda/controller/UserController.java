package com.cyber.difenda.controller;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cyber.difenda.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController extends BaseController {

    private final UserService userService;

    /* Endpoint to set roles for a user in Firebase token
    @PostMapping("/setRoles")
    public ResponseEntity<String> setRoles(@RequestParam String uid) throws Exception {
        userService.setRolesInToken(uid);
        System.out.println("uid"+uid);
        return ResponseEntity.ok("Roles set in Firebase token");
    }

    // Protected endpoint: verify token and get roles
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) throws Exception {
        String idToken = authHeader.replace("Bearer ", "");
        var roles = userService.getRolesFromToken(idToken);
        return ResponseEntity.ok(Map.of("roles", roles));
    }*/
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") Long id) throws Exception {
        var user = userService.getUserById(id);
        return ResponseEntity.ok(Map.of("user", user));
    }
}

