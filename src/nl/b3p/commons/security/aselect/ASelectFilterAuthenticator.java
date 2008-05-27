/*
 * $Id$
 */

package nl.b3p.commons.security.aselect;

import org.securityfilter.authenticator.*;
import org.securityfilter.config.SecurityConfig;
import org.securityfilter.filter.*;
import org.securityfilter.realm.SecurityRealmInterface;

import javax.servlet.FilterConfig;
import javax.servlet.http.*;
import java.io.IOException;
import java.security.Principal;

/**
 * AFilterSelectAuthenticator - authenticator implementation voor gebruik icm
 * nl.b3p.commons.security.aselect.ASelectAuthenticationFilter
 *
 * Dat filter moet nog eens worden herschreven naar alleen een authenticator
 * voor optimaal gebruik icm SecurityFilter. Voor nu moet het SecurityFilter
 * filter na ASelectAuthorizationFilter in de webapp geconfigureerd worden. Deze
 * authenticator doet dan praktisch hetzelfde als de BasicAuthenticator
 * behalve dat deze authenticator de credentials uit de ASelectTicket
 * op de sessie haalt ipv uit de Authorization header.
 */
public class ASelectFilterAuthenticator implements Authenticator {

    protected SecurityRealmInterface realm;
    protected String realmName;

    /**
     * Initialize this Authenticator.
     *
     * @param filterConfig
     * @param securityConfig
     */
    public void init(FilterConfig filterConfig, SecurityConfig securityConfig) throws Exception {
        System.out.println("init!!");               
        realm = securityConfig.getRealm();
        realmName = securityConfig.getRealmName();
    }

    /**
     * Returns ASELECTFILTER as the authentication method.
     *
     * @return ASELECTFILTER
     */
    public String getAuthMethod() {
        return "ASELECTFILTER";
    }

    /**
     * Process any login information that was included in the request, if any.
     * Returns true if SecurityFilter should abort further processing after the method completes (for example, if a
     * redirect was sent as part of the login processing).
     *
     * @param request
     * @param response
     * @return true if the filter should return after this method ends, false otherwise
     */
    public boolean processLogin(SecurityRequestWrapper request, HttpServletResponse response) throws Exception {
        if(request.getUserPrincipal() == null) {
            // attempt to dig out authentication info only if the user has not yet been authenticated

            ASelectTicket ticket = ASelectTicket.getFromSession(request.getSession());

            if(ticket == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Geen A-Select ticket!");
                return true;
            }

            Principal principal = realm.authenticate(ticket.getUid(), "");
            if(principal != null) {
                request.setUserPrincipal(principal);
            } else {
                 response.sendError(HttpServletResponse.SC_FORBIDDEN, "A-Select gebruikersnaam niet geauthoriseerd bij deze applicatie, neem contact op met de beheerder!");
                 return true;
            }
        }
        return false;
   }

    public void showLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
    }

    /**
     * Return true if security checks should be bypassed for this request.
     * Always returns false for this authenticator.
     *
     * @param request
     * @param patternMatcher
     * @return always returns false
    */
   public boolean bypassSecurityForThisRequest(SecurityRequestWrapper request, URLPatternMatcher patternMatcher) {
      return false;
   }

    /**
     * Return true if this is a logout request.
     * Always returns false for BASIC authenticator.
     *
     * @param request
     * @param response
     * @param patternMatcher
     * @return always returns false
     */
    public boolean processLogout(SecurityRequestWrapper request, HttpServletResponse response, URLPatternMatcher patternMatcher) {
        return false;
    }

}