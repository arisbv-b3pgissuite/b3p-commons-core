/*
 * B3P Commons Core is a library with commonly used classes for webapps.
 * Included are clieop3, oai, security, struts, taglibs and other
 * general helper classes and extensions.
 *
 * Copyright 2000 - 2008 B3Partners BV
 * 
 * This file is part of B3P Commons Core.
 * 
 * B3P Commons Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * B3P Commons Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with B3P Commons Core.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * $Id: ParameterLookupDispatchAction.java 2993 2006-03-27 06:21:31Z Chris $
 */
package nl.b3p.commons.struts;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Deze abstracte <b>DispatchAction</b> dispatcht naar een publieke methode
 * welke genoemd is in een Map van parameter naar methode naam. Indien het
 * request een parameter uit de Map met niet-lege waarde bevat wordt de
 * bijbehorende methode aangeroepen. Indien er geen methode kan worden gevonden
 * wordt <code>unspecified()</code> aangeroepen.
 * <p>
 * Indien het request was gecancelled (door het indrukken van een
 * <code>html:cancel</code> knop) wordt <code>cancelled()</code> aangeroepen.
 * <p>
 * Met deze action kunnen verschillende methodes van een Action class worden
 * aangeroepen door een methode in het path deel van de url op te nemen;
 * bijvoorbeeld:
 * <p>
 * <code>
 * http://www.b3p.nl/servlet/method<br>
 * </code>
 */
public abstract class UrlPathDispatchAction extends DynaFormDispatchAction {

    private static final String DISPATCHED_PARAMETER = UrlPathDispatchAction.class.getName() + ".DISPATCHED_PARAMETER";
    private static final String DISPATCHED_METHOD_NAME = UrlPathDispatchAction.class.getName() + ".DISPATCHED_METHOD_NAME";
    /** Mapping van parameter naar methode naam
     */
    /* Initialiseer deze met lege HashMap zodat op de monitor ervan kan worden gesynchronized */
    protected Map parameterMethodMap = new HashMap();

    protected abstract Map getParameterMethodMap();

    /* index van methode in array: 0-base*/
    /* /edit/1/100001/title */
    /* 0     1 2      3     */
    protected int getMethodPathIndex() {
        return 0;
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        if (isCancelled(request)) {
            ActionForward af = cancelled(mapping, form, request, response);
            if (af != null) {
                return af;
            }
        }

        String methodParameter = getMethodParameter(mapping, form, request, response);

        /* dispatchMethod() zal unspecified() aanroepen indien methodName null is */
        String methodName = setDispatchMethod(methodParameter, request);

        ActionForward af = dispatchMethod(mapping, form, request, response, methodName);

        return af;
    }

    /**
     * Plaatst de parameter en de methode op de request, protected om subklassen
     * de gelegenheid te geven dit aan te passen.
     */
    protected String setDispatchMethod(String methodParameter, HttpServletRequest request) {
        String methodName = null;
        if (methodParameter != null) {
            methodName = (String) parameterMethodMap.get(methodParameter);
        }
        request.setAttribute(DISPATCHED_PARAMETER, methodParameter);
        request.setAttribute(DISPATCHED_METHOD_NAME, methodName);
        return methodName;
    }

    /**
     * Geeft de naam van de methode waarnaar is gedispatcht of null indien het
     * request cancelled is of de methode unspecified is.
     */
    protected String getDispatchedMethodName(HttpServletRequest request) {
        return (String) request.getAttribute(DISPATCHED_METHOD_NAME);
    }

    /**
     * Geeft de naam van de request parameter die gebruikt is om de methode
     * op te zoeken waarnaar te dispatchen of null indien cancelled of
     * unspecified.
     */
    protected String getDispatchedParameter(HttpServletRequest request) {
        return (String) request.getAttribute(DISPATCHED_PARAMETER);
    }

    protected String getMethodParameter(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        /* Check met lock op parameterMethodMap indien deze leeg is en zo ja
         * initialiseer deze. Lezen kan zonder lock
         */
        synchronized (parameterMethodMap) {
            if (parameterMethodMap.isEmpty()) {
                Map realizedParameterMethodMap = getParameterMethodMap();
                if (realizedParameterMethodMap == null || realizedParameterMethodMap.isEmpty()) {
                    throw new IllegalStateException("empty parameterMethodMap");
                }
                parameterMethodMap.putAll(realizedParameterMethodMap);
            }
        }

        String[] pathInfo = parseParameter(mapping, request);
        int methodPathIndex = getMethodPathIndex();
        String methodParameter = null;
        if (pathInfo != null && methodPathIndex >= 0 && pathInfo.length > methodPathIndex) {
            methodParameter = pathInfo[methodPathIndex];
        }
        String methodName = null;
        if (methodParameter != null) {
            methodName = (String) parameterMethodMap.get(methodParameter);
        }
        if (methodName != null) {
            return methodParameter;
        }
        return null;
    }

    protected String[] parseParameter(ActionMapping mapping, HttpServletRequest request) {
        String[] pathInfo = null;
        String pi = mapping.getParameter();
        if (pi == null || pi.length() == 0) {
            return null;
        }
        if (pi.indexOf("/") == 0) {
            pathInfo = pi.substring(1).split("/");
        } else {
            pathInfo = pi.split("/");
        }
        return pathInfo;
    }
}