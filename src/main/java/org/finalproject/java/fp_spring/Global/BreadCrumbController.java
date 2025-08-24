package org.finalproject.java.fp_spring.Global;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class BreadCrumbController {

    @ModelAttribute("breadcrumb")
    public List<Map<String, String>> addBreadCrumb(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String returnTo = request.getParameter("returnTo");
        String isService = request.getParameter("isService");
        String isOperator = request.getParameter("isOperator");

        boolean serviceFlag = Boolean.parseBoolean(isService);
        boolean operatorFlag = Boolean.parseBoolean(isOperator);

        List<Map<String, String>> breadcrumb = new ArrayList<>();

        breadcrumb.add(Map.of("label", "Home", "url", "/"));

        // Company
        if (uri.startsWith("/admin/company")) {
            breadcrumb.add(Map.of("label", "Dashboard", "url", "/admin/company"));

            if (uri.matches("/admin/company/edit/\\d+")) {

                breadcrumb.add(Map.of("label", "Edit Company Details", "url", uri));
            } else if (uri.matches("/admin/company/\\d+")) {
                // show
                breadcrumb.add(Map.of("label", "View Company Details", "url", uri));
            } else if (uri.contains("/create")) {
                breadcrumb.add(Map.of("label", "Create Company", "url", uri));
            }

            // Users
        } else if (uri.startsWith("/admin/users")) {
            breadcrumb.add(Map.of("label", "Dashboard", "url", "/admin/company"));

            // index / show
            if (uri.matches("/admin/users/\\d+")) {
                if (serviceFlag) {
                    String label = operatorFlag ? "Employees" : "Customers";
                    breadcrumb.add(Map.of("label", "Company Details", "url", returnTo));
                    breadcrumb.add(Map.of("label", label, "url", returnTo));
                } else {
                    breadcrumb.add(Map.of("label", "Users", "url", uri));
                }

                // edit
            } else if (uri.matches("/admin/users/edit/\\d+")) {
                if (serviceFlag) {
                    String label = operatorFlag ? "Employees" : "Customers";
                    breadcrumb.add(Map.of("label", "Company Details", "url", returnTo));
                    breadcrumb.add(Map.of("label", label, "url", returnTo));
                } else {
                    breadcrumb.add(Map.of("label", "Customers", "url", returnTo));
                }
                breadcrumb.add(Map.of("label", "Edit User", "url", uri));

                // Create
            } else if (uri.contains("/create")) {
                breadcrumb.add(Map.of("label", "Users", "url", returnTo));
                breadcrumb.add(Map.of("label", "Create User", "url", uri));
            }
        }

        return breadcrumb;
    }

}
