/*
 * $Id: CrudAction.java 2964 2006-03-23 10:30:17Z Matthijs $
 */

package nl.b3p.commons.struts;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.struts.validator.DynaValidatorForm;

public class CrudAction extends ExtendedMethodAction {
    
    private static final Log log = LogFactory.getLog(CrudAction.class);
    
    protected static final String GENERAL_ERROR_KEY = "error.exception";
    protected static final String TOKEN_ERROR_KEY = "error.token";
    protected static final String NOTFOUND_ERROR_KEY = "error.notfound";
    protected static final String VALIDATION_ERROR_KEY = "error.validation";
    
    protected static final String SUCCESS = "success";
    protected static final String FAILURE = "failure";
    protected static final String LISTFW = "success";
    
    protected static final String DEFAULT_FORWARDFIELD = "action";
    protected static final String ALTERNATE_FORWARDFIELD = "alt_action";
    
    protected static final String CONFIRM = "confirm";
    protected static final String DELETE_CONFIRM = "deleteConfirm";
    protected static final String SAVE_CONFIRM = "saveConfirm";
    protected static final String DELETE = "delete";
    protected static final String CREATE = "create";
    protected static final String SAVE = "save";
    protected static final String EDIT = "edit";
    protected static final String LIST = "list";
    
    protected Map getActionMethodPropertiesMap() {
        Map map = new HashMap();
        
        ExtendedMethodProperties crudProp = null;
        
        crudProp = new ExtendedMethodProperties(CONFIRM);
        map.put(CONFIRM, crudProp);
        
        crudProp = new ExtendedMethodProperties(DELETE_CONFIRM);
        crudProp.setDefaultForwardName(SUCCESS);
        crudProp.setDefaultMessageKey("warning.crud.delete");
        map.put(DELETE_CONFIRM, crudProp);
        
        crudProp = new ExtendedMethodProperties(SAVE_CONFIRM);
        crudProp.setDefaultForwardName(SUCCESS); // Na saveConfirm forward naar success
        crudProp.setDefaultMessageKey("warning.crud.save");
        map.put(SAVE_CONFIRM, crudProp);
        
        crudProp = new ExtendedMethodProperties(DELETE);
        crudProp.setDefaultForwardName(LISTFW);
        crudProp.setDefaultMessageKey("warning.crud.deletedone");
        crudProp.setAlternateForwardName(FAILURE);
        crudProp.setAlternateMessageKey("error.crud.deletefailed");
        map.put(DELETE, crudProp);
        
        crudProp = new ExtendedMethodProperties(CREATE);
        crudProp.setDefaultForwardName(SUCCESS);
        crudProp.setAlternateForwardName(LISTFW);
        map.put(CREATE, crudProp);
        
        crudProp = new ExtendedMethodProperties(SAVE);
        crudProp.setDefaultForwardName(SUCCESS);
        crudProp.setDefaultMessageKey("warning.crud.savedone");
        crudProp.setAlternateForwardName(FAILURE);
        crudProp.setAlternateMessageKey("error.crud.savefailed");
        map.put(SAVE, crudProp);
        
        crudProp = new ExtendedMethodProperties(EDIT);
        crudProp.setDefaultForwardName(SUCCESS);
        crudProp.setAlternateForwardName(LISTFW);
        map.put(EDIT, crudProp);
        
        crudProp = new ExtendedMethodProperties(LIST);
        crudProp.setDefaultForwardName(LISTFW);
        crudProp.setAlternateForwardName(FAILURE);
        map.put(LIST, crudProp);
        
        return map;
    }
    
    protected void createLists(DynaValidatorForm form, HttpServletRequest request) throws Exception {}
    
    protected ActionForward getUnspecifiedDefaultForward(ActionMapping mapping, HttpServletRequest request) {
        return mapping.findForward(SUCCESS);
    }
    
    protected void prepareMethod(ActionForm form, HttpServletRequest request, String def, String alt) throws Exception {
        // nieuwe default actie zetten
        DynaValidatorForm dynaForm = (DynaValidatorForm)form;
        dynaForm.set(DEFAULT_FORWARDFIELD, def);
        dynaForm.set(ALTERNATE_FORWARDFIELD, alt);
        createLists(dynaForm, request);
    }
    
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        prepareMethod(form, request, EDIT, LIST);
        addDefaultMessage(mapping, request);
        return mapping.findForward(SUCCESS);
    }
    
    public ActionForward deleteConfirm(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        prepareMethod(form, request, DELETE, EDIT);
        addDefaultMessage(mapping, request);
        return getDefaultForward(mapping, request);
    }
    
    public ActionForward saveConfirm(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        prepareMethod(form, request, SAVE, EDIT);
        addDefaultMessage(mapping, request);
        return getDefaultForward(mapping, request);
    }
    
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        prepareMethod(form, request, EDIT, LIST);
        addDefaultMessage(mapping, request);
        return getDefaultForward(mapping, request);
    }
    
    public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        prepareMethod(form, request, LIST, EDIT);
        addDefaultMessage(mapping, request);
        return getDefaultForward(mapping, request);
    }
    
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        prepareMethod(form, request, LIST, EDIT);
        addDefaultMessage(mapping, request);
        return getDefaultForward(mapping, request);
    }
    
    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // nieuwe default actie op delete zetten
        DynaValidatorForm dynaForm = (DynaValidatorForm)form;
        dynaForm.initialize(mapping);
        prepareMethod(form, request, LIST, EDIT);
        addDefaultMessage(mapping, request);
        return getDefaultForward(mapping, request);
    }
    
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // nieuwe default actie op delete zetten
        DynaValidatorForm dynaForm = (DynaValidatorForm)form;
        dynaForm.initialize(mapping);
        prepareMethod(form, request, EDIT, LIST);
        addDefaultMessage(mapping, request);
        return getDefaultForward(mapping, request);
    }
    
    public ActionForward confirm(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return redispatchFormField(mapping, form, request, response, DEFAULT_FORWARDFIELD);
    }
    
    public ActionForward cancelled(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return redispatchFormField(mapping, form, request, response, ALTERNATE_FORWARDFIELD);
    }
    
    public ActionForward execute(ActionMapping mapping, ActionForm  form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        ActionForward forward = super.execute(mapping, form, request, response);
        /* Check van token in de implementatie van save en delete */
        saveToken(request);
        return forward;
    }
    
}