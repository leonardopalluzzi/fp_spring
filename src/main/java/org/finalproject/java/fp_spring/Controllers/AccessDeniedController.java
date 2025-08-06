package org.finalproject.java.fp_spring.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

// probabilmente si puo rimuovere
@Controller
public class AccessDeniedController {

    @GetMapping("/access-denied")
    public String accessDenied(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login?logout";
    }
}
