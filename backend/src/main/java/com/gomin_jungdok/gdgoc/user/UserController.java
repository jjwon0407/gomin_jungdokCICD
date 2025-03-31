package com.gomin_jungdok.gdgoc.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    /*private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userService.saveUser(
                user.getId(),
                user.getPassword(),
                user.getNickname(),
                user.getGoogleEmail(),
                user.getSocialId(),
                user.getSocialType()
        );
        return ResponseEntity.ok(savedUser);
    }*/
}

