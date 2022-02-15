/*
 * Copyright 2007,2016 EUROPEAN UNION
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://ec.europa.eu/isa2/solutions/european-union-public-licence-eupl_en
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * Date: 2020/05/11
 * Authors:
 * European Commission, Joint Research Centre - jrc-inspire-support@ec.europa.eu
 *
 * This work was supported by the Interoperability solutions for public
 * administrations, businesses and citizens programme (http://ec.europa.eu/isa2)
 * through Action 2016.10: European Location Interoperability Solutions for e-Government (ELISE)
 */
package eu.europa.ec.re3gistry2.base.utility;

import eu.cec.digit.ecas.client.jaas.DetailedUser;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegUserManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegUserRegGroupMappingManager;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItemRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegItemproposedRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegRole;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.model.RegUserRegGroupMapping;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.Factory;

public class UserHelper {

    private UserHelper() {
    }

    public static RegUser checkUser(HttpServletRequest request) {
        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();
        RegUser regUser = null;
        HttpSession session = request.getSession();

//        try {
        //Init persistence unit
        EntityManager em = null;
        try {
            em = PersistenceFactory.getEntityManagerFactory().createEntityManager();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (em != null) {
            //Init variables
            String loginType = configuration.getProperties().getProperty(BaseConstants.KEY_PROPERTY_LOGIN_TYPE, BaseConstants.KEY_PROPERTY_LOGIN_TYPE_SHIRO);
            RegUserManager regUserManager = new RegUserManager(em);

            //Checking the Re3gistry2 user in session (user from ecas remapped in Re3gistry user).
            regUser = (RegUser) session.getAttribute(BaseConstants.KEY_SESSION_USER);

            //Handling ECAS login type
            switch (loginType) {
                case BaseConstants.KEY_PROPERTY_LOGIN_TYPE_ECAS: {
                    //Taking the DetailedUser from session
                    DetailedUser detailedUser = (DetailedUser) request.getUserPrincipal();
                    //If the RegUser is already in the session, return the user
                    if (regUser == null) {
                        //If the RegUser is not already in the session, check
                        //the DetailedUser
                        if (detailedUser != null && detailedUser.getEmail() != null) {
                            //Getting the user from the system using ECAS email as key.
                            try {
                                regUser = regUserManager.findByEmail(detailedUser.getEmail().toLowerCase());
                            } catch (Exception e) {
                                request.setAttribute(BaseConstants.KEY_REQUEST_USER_ERROR_MESSAGES, BaseConstants.KEY_REQUEST_USER_NOT_AVAILABLE);
                                logger.info("@ The user " + detailedUser.getEmail() + " is not available in the system.", e);
                                regUser = null;
                            }
                        } else {
                            request.setAttribute(BaseConstants.KEY_REQUEST_USER_ERROR_MESSAGES, BaseConstants.KEY_REQUEST_USER_NOT_LOGGEDIN);
                            regUser = null;
                        }
                    }
                    break;
                } //Handling SHIRO login type
                case BaseConstants.KEY_PROPERTY_LOGIN_TYPE_SHIRO: {
                    // init shiro - place this e.g. in the constructor
                    Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory();
                    org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
                    SecurityUtils.setSecurityManager(securityManager);
                    String username = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_USERNAME);
                    String password = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_PASSWORD);
                    String rememberMe = request.getParameter(BaseConstants.KEY_FORM_FIELD_NAME_REMEMBERME);
                    //Taking the DetailedUser from session
                    org.apache.shiro.subject.Subject detailedUser = SecurityUtils.getSubject();
                    if (regUser == null && (username != null && password != null)) {
                        if (detailedUser != null && tryLoginWithSHIRO(username, password, Boolean.valueOf(rememberMe), request)) {

                            //Getting the user from the system using SHIRO username as key.
                            try {
                                regUser = regUserManager.findByEmail(((String) detailedUser.getPrincipal()).toLowerCase());
                                request.setAttribute(BaseConstants.KEY_SESSION_USER, regUser);
                            } catch (Exception e) {
                                request.setAttribute(BaseConstants.KEY_REQUEST_USER_ERROR_MESSAGES, BaseConstants.KEY_REQUEST_USER_NOT_AVAILABLE);
                                logger.info("@ The user " + (String) detailedUser.getPrincipal() + " is not available in the system.", e);
                                regUser = null;
                                detailedUser.logout();
                                request.getSession().invalidate();
                            }
                        } else {
//                        request.setAttribute(BaseConstants.KEY_REQUEST_USER_ERROR_MESSAGES, BaseConstants.KEY_REQUEST_USER_NOT_LOGGEDIN);
                            regUser = null;
                            if (detailedUser != null) {
                                detailedUser.logout();
                                request.getSession().invalidate();
                            }
                        }
                    }
                    break;
                }
                default:
                    request.setAttribute(BaseConstants.KEY_REQUEST_USER_ERROR_MESSAGES, BaseConstants.KEY_REQUEST_LOGIN_CONFIGURATION_ERRORR);
                    logger.error("@ Configuration error: login type not properly configured.");
                    regUser = null;
                    break;
            }

            if (regUser != null) {
                if (regUser.getEnabled()) {
                    // If the user is enabled, set the session variable
                    session.setAttribute(BaseConstants.KEY_SESSION_USER, regUser);

                    // If the permissions of the RegUser is not in the session, load them                    
                    if (session.getAttribute(BaseConstants.KEY_SESSION_USERPERGROUPSMAP) == null) {

                        // Getting the RegUserRegGroupMapping
                        RegUserRegGroupMappingManager regUserRegGroupMappingManager = new RegUserRegGroupMappingManager(em);
                        List<RegUserRegGroupMapping> regUserRegGroupMappings = null;
                        try {
                            regUserRegGroupMappings = regUserRegGroupMappingManager.getAll(regUser);
                        } catch (Exception ex) {
                            logger.info("There is a problem in getting the groups of the user " + regUser.getEmail(), ex.getMessage());
                        }

                        // Preparing the hashmap
                        HashMap<String, RegGroup> userGroups = new HashMap<>();

                        for (RegUserRegGroupMapping tmp : regUserRegGroupMappings) {
                            userGroups.put(tmp.getRegGroup().getUuid(), tmp.getRegGroup());
                        }

                        // Setting the Groups and Roles in session
                        session.setAttribute(BaseConstants.KEY_SESSION_USERPERGROUPSMAP, userGroups);
                    }

                } else {
                    //If the user is not enabled, set the error request attribute
                    request.setAttribute(BaseConstants.KEY_REQUEST_USER_ERROR_MESSAGES, BaseConstants.KEY_REQUEST_USER_NOT_ENABLED);
                    regUser = null;
                }
            }
//        } catch (Exception e) {
//            logger.error("@ UserHelper.checkCurrentUserKey: generic error.", e);
//            regUser = null;
//            request.getSession().invalidate();
//        }
        }
        return regUser;
    }

    // login
    public static boolean tryLoginWithSHIRO(String username, String password, Boolean rememberMe, HttpServletRequest request) {
        // get the currently executing user:
        org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();

        if (!currentUser.isAuthenticated()) {
            //collect user principals and credentials in a gui specific manner
            //such as username/password html form, X509 certificate, OpenID, etc.
            //We'll use the username/password example here since it is the most common.
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            //this is all you have to do to support 'remember me' (no config - built in!):
            token.setRememberMe(rememberMe);

            try {
                currentUser.login(token);
                System.out.println("User [" + currentUser.getPrincipal().toString() + "] logged in successfully.");

                // save current username in the session, so we have access to our User model
                currentUser.getSession().setAttribute(BaseConstants.KEY_FORM_FIELD_NAME_USERNAME, username);
                return true;
            } catch (UnknownAccountException uae) {
                request.setAttribute(BaseConstants.KEY_REQUEST_USER_ERROR_MESSAGES, BaseConstants.KEY_REQUEST_USER_UNKNOWN_ACCOUNT_EXCEPTION);
            } catch (IncorrectCredentialsException ice) {
                request.setAttribute(BaseConstants.KEY_REQUEST_USER_ERROR_MESSAGES, BaseConstants.KEY_REQUEST_USER_INCORECT_CREDENTIALS_EXCEPTION);
            } catch (LockedAccountException lae) {
                request.setAttribute(BaseConstants.KEY_REQUEST_USER_ERROR_MESSAGES, BaseConstants.KEY_REQUEST_USER_LOCKED_ACCOUNT_EXCEPTION);
            }
        } else {
            return true; // already logged in
        }
        return false;
    }

    public static boolean checkRegItemAction(String[] actionString, Map currentUserGroupsMap, List<RegItemRegGroupRegRoleMapping> itemMappings) {

        boolean checkAction = false;

        // Comparing RegGroups associated with the user and RegGroups of the current mapping
        // and creating the list of action allowed by the current user
        HashMap<String, String> currentUserPermittedActions = new HashMap();
        for (RegItemRegGroupRegRoleMapping tmp : itemMappings) {
            if (currentUserGroupsMap.containsKey(tmp.getRegGroup().getUuid())) {
                HashMap allowedActions = UserHelper.getRoleActions(tmp.getRegRole());
                currentUserPermittedActions.putAll(allowedActions);
            }
        }

        // Checking if the current user has the permission on the current action
        for (String tmp : actionString) {
            if (currentUserPermittedActions.containsKey(tmp)) {
                checkAction = true;
                break;
            }
        }

        return checkAction;
    }

    public static boolean checkRegItemproposedAction(String[] actionString, Map currentUserGroupsMap, List<RegItemproposedRegGroupRegRoleMapping> itemproposedMappings) {

        boolean checkAction = false;

        // Comparing RegGroups associated with the user and RegGroups of the current mapping
        // and creating the list of action allowed by the current user
        HashMap<String, String> currentUserPermittedActions = new HashMap();
        for (RegItemproposedRegGroupRegRoleMapping tmp : itemproposedMappings) {
            if (currentUserGroupsMap.containsKey(tmp.getRegGroup().getUuid())) {
                HashMap allowedActions = UserHelper.getRoleActions(tmp.getRegRole());
                currentUserPermittedActions.putAll(allowedActions);
            }
        }

        // Checking if the current user has the permission on the current action
        for (String tmp : actionString) {
            if (currentUserPermittedActions.containsKey(tmp)) {
                checkAction = true;
                break;
            }
        }

        return checkAction;
    }

    public static boolean checkGenericAction(String[] actionString, Map currentUserGroupsMap, RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager) {
        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();
        boolean checkAction = false;

        try {
            HashMap<String, String> currentUserPermittedActions = new HashMap();

            // Getting the groups of the current user
            if (currentUserGroupsMap != null) {
                Iterator it = currentUserGroupsMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    RegGroup tmp = (RegGroup) pair.getValue();

                    List<RegItemRegGroupRegRoleMapping> tmpMappings = regItemRegGroupRegRoleMappingManager.getAll(tmp);

                    for (RegItemRegGroupRegRoleMapping tmpMap : tmpMappings) {
                        HashMap allowedActions = UserHelper.getRoleActions(tmpMap.getRegRole());
                        currentUserPermittedActions.putAll(allowedActions);
                    }
                }
            }

            // Checking if the current user has the permission on the current action
            for (String tmp : actionString) {
                if (currentUserPermittedActions.containsKey(tmp)) {
                    checkAction = true;
                    break;
                }
            }

        } catch (Exception e) {
            logger.error("@ UserHelper.checkGenericAction: generic error.", e);
            checkAction = false;
        }

        return checkAction;
    }

    private static HashMap<String, String> getRoleActions(RegRole regRole) {
        HashMap<String, String> tmpList = new HashMap();

        Configuration configuration = Configuration.getInstance();
        String tmp = configuration.getProperties().getProperty(BaseConstants.KEY_PROPERTY_ROLEPERMISSION_SUFFIX + regRole.getLocalid());
        String[] actions = tmp.split(",");

        for (String action : actions) {
            tmpList.put(action, action);
        }

        return tmpList;
    }

    public static boolean checkUserRole(RegUser regUser, RegRole regRole, RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager, RegUserRegGroupMappingManager regUserRegGroupMappingManager) throws Exception {

        boolean outValue = false;

        try {
            // Getting the list of mapping that contains the specified role
            List<RegItemRegGroupRegRoleMapping> tmps = regItemRegGroupRegRoleMappingManager.getAll(regRole);

            if (tmps != null && !tmps.isEmpty()) {
                // Checking if the user is in the group of the specified role
                for (RegItemRegGroupRegRoleMapping tmp : tmps) {
                    try {
                        regUserRegGroupMappingManager.get(regUser, tmp.getRegGroup());
                        outValue = true;
                        break;

                    } catch (NoResultException e) {
                    }
                }
            }

        } catch (NoResultException e) {
        }

        return outValue;

    }

    public static void generatePassword(RegUser regUser, String key) {
        RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        Object salt = rng.nextBytes();

        // Now hash the plain-text password with the random salt and multiple
        // iterations and then Base64-encode the value (requires less space than Hex):
        String hashedPasswordBase64 = new Sha256Hash(key, salt, 1024).toBase64();

        regUser.setShiropassword(hashedPasswordBase64);
        regUser.setShirosalt(salt.toString());
    }

    public static String generatePassword(String key, String salt) {
        // Now hash the plain-text password with the random salt and multiple
        // iterations and then Base64-encode the value (requires less space than Hex):
        String hashedPasswordBase64 = new Sha256Hash(key, salt, 1024).toBase64();
        return hashedPasswordBase64;
    }

    public static boolean checkCurrentUserKey(String email, String key) {
        //Init configurations
        Configuration configuration = Configuration.getInstance();
        Logger logger = configuration.getLogger();

        //Init persistence unit
        EntityManager em = null;
        try {
            em = PersistenceFactory.getEntityManagerFactory().createEntityManager();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (em != null) {
            //Init variables
            String loginType = configuration.getProperties().getProperty(BaseConstants.KEY_PROPERTY_LOGIN_TYPE, BaseConstants.KEY_PROPERTY_LOGIN_TYPE_SHIRO);
            RegUserManager regUserManager = new RegUserManager(em);

            //Handling ECAS login type
            switch (loginType) {
                case BaseConstants.KEY_PROPERTY_LOGIN_TYPE_ECAS: {
                    // TODO
                    break;
                } //Handling SHIRO login type
                case BaseConstants.KEY_PROPERTY_LOGIN_TYPE_SHIRO: {
                    // init shiro - place this e.g. in the constructor
                    Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory();
                    org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
                    SecurityUtils.setSecurityManager(securityManager);
                    //Taking the DetailedUser from session
                    if (email != null && key != null) {
                        return tryLoginWithSHIRO(email, key);
                    }
                    break;
                }
                default:
                    logger.error("@ Configuration error: login type not properly configured.");
                    break;
            }

        }
        return false;
    }

    public static boolean tryLoginWithSHIRO(String email, String key) {
        org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();

        //collect user principals and credentials in a gui specific manner
        //such as username/password html form, X509 certificate, OpenID, etc.
        //We'll use the username/password example here since it is the most common.
        UsernamePasswordToken token = new UsernamePasswordToken(email, key, Boolean.FALSE);

        try {
            currentUser.login(token);
            System.out.println("User [" + currentUser.getPrincipal().toString() + "] logged in successfully.");

            return true;
        } catch (UnknownAccountException | IncorrectCredentialsException | LockedAccountException uae) {
            return false;
        }

    }
}
