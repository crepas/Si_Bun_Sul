package com.sys.OrderSystem.Controller;

import com.sys.OrderSystem.Entity.Administrator;
import com.sys.OrderSystem.Service.AdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpSession;

@Controller

public class AdminController {

    @Autowired
    private AdministratorService administratorService;

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        Administrator admin = administratorService.login(username, password);
        if (admin != null) {
            session.setAttribute("admin", admin);
            return "menumanagement";  // 로그인 성공 시 대시보드로 이동
        }
        model.addAttribute("error", "Invalid username or password");
        return "login";
    }

    @GetMapping("/join")
    public String joinPage() {
        return "join";
    }

    @PostMapping("/join")
    public String join(@ModelAttribute Administrator administrator, Model model) {
        if (administratorService.registerAdmin(administrator)) {
            return "redirect:/login";
        }
        model.addAttribute("error", "Username or email already exists");
        return "join";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
    @GetMapping("/findpasswd")
    public String findPasswordPage() {
        return "findpasswd";
    }

    @PostMapping("/findpasswd/verify")
    @ResponseBody
    public ResponseEntity<String> verifyUsername(@RequestParam String username) {
        boolean exists = administratorService.checkUsernameExists(username);
        if (exists) {
            // 실제로는 여기서 이메일이나 SMS로 인증번호를 보내야 함
            return ResponseEntity.ok("verified");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
    }

    @PostMapping("/findpasswd")
    public String updatePassword(@RequestParam String username,
                                 @RequestParam String newPassword,
                                 Model model) {
        boolean updated = administratorService.updatePassword(username, newPassword);
        if (updated) {
            return "redirect:/login";
        }
        model.addAttribute("error", "Password update failed");
        return "findpasswd";
    }
}