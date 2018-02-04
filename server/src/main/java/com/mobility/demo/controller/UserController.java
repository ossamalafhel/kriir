package com.mobility.demo.controller;

import com.mobility.demo.model.Car;
import com.mobility.demo.model.User;
import com.mobility.demo.model.repository.UserRepository;
import com.mobility.demo.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private List<FluxSink<User>> userHandlers = new ArrayList<>();

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/allusers")
    List<User> allUsers() {
        return userRepository.findAll();
    }

    @ApiOperation(
            value = "save car",
            produces = MediaType.APPLICATION_JSON_VALUE,
            response = Car.class
    )
    @PostMapping("/saveUser")
    @ResponseStatus(HttpStatus.OK)
    public void saveUser(@ModelAttribute User save) {
        userService.save(save);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.GET, value = "/users")
    @ApiOperation(
            value = "Retrieve all users",
            produces = MediaType.APPLICATION_JSON_VALUE,
            response = User.class
    )

    public Flux<User> users() {
        return Flux.push(sink -> {
            userHandlers.add(sink);
            sink.onCancel(() -> userHandlers.remove(sink));
        });
    }

    public void handleUser(User user) {
        userHandlers.forEach(han -> han.next(user));
    }

}
