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
        String isService = request.getParameter("isService");
    
        List<Map<String, String>> breadcrumb = new ArrayList<>();

        breadcrumb.add(Map.of("label", "Home", "url", "/"));

        if(uri.startsWith("/admin/company")){
            if(uri.matches("/admin/company/edit/\\d+")){
                breadcrumb.add(Map.of("label", "Dashboard", "url", "/admin/company"));
                breadcrumb.add(Map.of("label", "Edit Company Details", "url", uri));
            } else if(uri.matches("/admin/company/\\d+")){
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
            breadcrumb.add(Map.of("label", "Dashboard", "url", "/admin/company"));
            if(uri.matches("/admin/users/\\d+")){
                //index
                if(isService != null){
                    
                    breadcrumb.add(Map.of("label", "Company Details", "url", returnTo));
                    breadcrumb.add(Map.of("label", "Users", "url", uri));
                } else {
                    breadcrumb.add(Map.of("label", "Users", "url", uri));
                }
                

            } else if(uri.matches("/admin/users/edit/\\d+")){
                if(isService != null){
                    breadcrumb.add(Map.of("label", "Company Details", "url", returnTo));
                    breadcrumb.add(Map.of("label", "Users", "url", returnTo));
                    breadcrumb.add(Map.of("label", "Edit User", "url", uri));

                } else {

                    breadcrumb.add(Map.of("label", "Users", "url", returnTo));
                    breadcrumb.add(Map.of("label", "Edit User", "url", uri));
                }
                
            } else if(uri.contains("/create")){
                breadcrumb.add(Map.of("label", "Users", "url", returnTo));
                breadcrumb.add(Map.of("label", "Create User", "url", uri));
            }
        }

        return breadcrumb;
    }
    
}
