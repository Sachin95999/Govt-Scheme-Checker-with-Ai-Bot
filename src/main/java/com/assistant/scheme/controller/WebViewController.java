package com.assistant.scheme.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebViewController {

    @GetMapping({"/", "/index.html"})
    public String index(HttpServletRequest request, Model model) {
        setThemeModel(request, model);
        return "index";
    }

    @GetMapping("/dashboard.html")
    public String dashboard(HttpServletRequest request, Model model) {
        setThemeModel(request, model);
        return "dashboard";
    }

    @GetMapping("/eligibility.html")
    public String eligibility(HttpServletRequest request, Model model) {
        setThemeModel(request, model);
        return "eligibility";
    }

    @GetMapping("/admin.html")
    public String admin(HttpServletRequest request, Model model) {
        setThemeModel(request, model);
        return "admin";
    }

    @GetMapping("/set-theme")
    public String setTheme(@RequestParam("theme") String theme, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("theme", theme);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 365); // 1 year
        response.addCookie(cookie);
        
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }
        return "redirect:/index.html";
    }

    private void setThemeModel(HttpServletRequest request, Model model) {
        String theme = "light";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("theme".equals(c.getName())) {
                    theme = c.getValue();
                    break;
                }
            }
        }
        model.addAttribute("theme", theme);
    }
}
