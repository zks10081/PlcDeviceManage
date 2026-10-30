package com.dmkj.plcdevice.controller;

import com.dmkj.plcdevice.common.Result;
import com.dmkj.plcdevice.dto.UserDTO;
import com.dmkj.plcdevice.entity.User;
import com.dmkj.plcdevice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Result<?> page(@RequestParam(required = false) String username,
                          @RequestParam(required = false) String nickname,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size) {
        return Result.success(userService.page(username, nickname, page, size));
    }

    @GetMapping("/{id}")
    public Result<User> get(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> create(@RequestBody UserDTO dto) {
        return Result.success(userService.create(dto));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> update(@RequestBody UserDTO dto) {
        return Result.success(userService.update(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }
}
