/*
 * $Id: ExtendedMethodAction.java 2964 2006-04-11 10:30:17Z Chris $
 */

package nl.b3p.commons.struts;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.struts.validator.DynaValidatorForm;

public abstract class ExtendedMethodAction extends MethodPropertiesAction {
    
    protected Class getActionMethodPropertiesClass() {
        return ExtendedMethodProperties.class;
    }
    
    protected abstract ActionForward getUnspecifiedDefaultForward(ActionMapping mapping, HttpServletRequest request);
    
    protected ActionForward getUnspecifiedAlternateForward(ActionMapping mapping, HttpServletRequest request) {
        ActionForward af = mapping.getInputForward();
        if (af!=null)
            return af;
        return getUnspecifiedDefaultForward(mapping, request);
    }
    
    public ActionForward redispatchFormField(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String formfield) throws Exception {
        String methodParameter = null;
        if (formfield!=null) {
            DynaValidatorForm dynaForm = (DynaValidatorForm)form;
            methodParameter = (String)dynaForm.get(formfield);
        }
        return redispatch(mapping, form, request, response, methodParameter);
    }
    
    public ActionForward redispatch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String methodParameter) throws Exception {
        String methodName = setDispatchMethod(methodParameter, request);
        return dispatchMethod(mapping, form, request, response, methodName);
    }
    
    protected void addDefaultMessage(ActionMapping mapping, HttpServletRequest request) {
        String defaultMessagekey = null;
        
        ExtendedMethodProperties props = (ExtendedMethodProperties)getMethodProperties(request);
        if (props != null) {
            defaultMessagekey = props.getDefaultMessageKey();
        }
        
        if(defaultMessagekey != null)
            addMessage(request, defaultMessagekey);
    }
    
    protected void addAlternateMessage(ActionMapping mapping, HttpServletRequest request, String causeKey) {
        addAlternateMessage(mapping, request, causeKey, null);
    }
    
    protected void addAlternateMessage(ActionMapping mapping, HttpServletRequest request, String causeKey, String cause) {
        if (cause == null) {
            MessageResources messages = getResources(request);
            Locale locale = getLocale(request);
            cause = messages.getMessage(locale, causeKey);
        }
        
        String alternateMessagekey = null;
        
        ExtendedMethodProperties props = (ExtendedMethodProperties)getMethodProperties(request);
        if(props != null) {
            alternateMessagekey = props.getAlternateMessageKey();
        }
        
        if(alternateMessagekey != null)
            addMessage(request, alternateMessagekey, cause);
        
    }
    
    protected ActionForward getAlternateForward(ActionMapping mapping, HttpServletRequest request) {
        String alternateMessagekey = null;
        ActionForward alternateForward = null;
        
        ExtendedMethodProperties props = (ExtendedMethodProperties)getMethodProperties(request);
        if(props != null) {
            alternateForward = mapping.findForward(props.getAlternateForwardName());
        }
        if (alternateForward != null)
            return alternateForward;
        
        return getUnspecifiedAlternateForward(mapping, request);
    }
    
    protected ActionForward getDefaultForward(ActionMapping mapping, HttpServletRequest request) {
        ActionForward defaultForward = null;
        
        ExtendedMethodProperties props = (ExtendedMethodProperties)getMethodProperties(request);
        if (props != null) {
            defaultForward = mapping.findForward(props.getDefaultForwardName());
        }
        
        if (defaultForward != null)
            return defaultForward;
        
        return getUnspecifiedDefaultForward(mapping, request);
    }
    
}