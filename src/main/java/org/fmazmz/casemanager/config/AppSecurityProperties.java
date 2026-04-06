package org.fmazmz.casemanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {

    /**
     * Internal admin users pre-provisioned at startup.
     * Example: ["admin@company.com"].
     */
    private List<String> bootstrapAdminEmails = new ArrayList<>();

    public List<String> getBootstrapAdminEmails() {
        return bootstrapAdminEmails;
    }

    public void setBootstrapAdminEmails(List<String> bootstrapAdminEmails) {
        this.bootstrapAdminEmails = bootstrapAdminEmails;
    }
}
