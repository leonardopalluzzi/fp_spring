package org.finalproject.java.fp_spring.Global;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class BreadCrumbController {

    @ModelAttribute("breadcrumb")
    public List<Map<String, String>> addBreadCrumb(HttpServletRequest request){
        String uri = request.getRequestURI();
        String returnTo = request.getParameter("returnTo");
    
        List<Map<String, String>> breadcrumb = new ArrayList<>();

        breadcrumb.add(Map.of("label", "Home", "url", "/"));

        if(uri.startsWith("/admin/company")){
            if(uri.matches("/admin/copmany/edit/\\d+")){
                breadcrumb.add(Map.of("label", "Dashboard", "url", "/admin/company"));
                breadcrumb.add(Map.of("label", "Edit Company Details", "url", uri));
            } else if(uri.matches("/admi/company/\\d+")){
                //show
                breadcrumb.add(Map.of("label", "Dashboard", "url", "/admin/company"));
                breadcrumb.add(Map.of("label", "View Company Details", "url", uri));
            } else if(uri.contains("/create")){
                breadcrumb.add(Map.of("label", "Dashboard", "url", "/admin/company"));
                breadcrumb.add(Map.of("label", "Create Company", "url", uri));
            } else {
                breadcrumb.add(Map.of("label", "Dashboard", "url", "/admin/company"));
            }

        } else if(uri.startsWith("/admin/users")){
            if(uri.matches("/admin/users/\\d+")){
                //index
            } else if(uri.matches("/admin/users/edit/\\d+")){

            } else if(uri.contains("/create")){

            }
        }

        return breadcrumb;
    }
    
}
