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

import eu.europa.ec.re3gistry2.crudimplementation.RegUserManager;
import eu.europa.ec.re3gistry2.model.RegUser;
import javax.persistence.EntityManager;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.logging.log4j.Logger;

public class RegistryRealm extends JdbcRealm {

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        Logger logger = Configuration.getInstance().getLogger();
        try {
            // identify account to log to
            UsernamePasswordToken userPassToken = (UsernamePasswordToken) token;
            final String username = userPassToken.getUsername();

            if (username == null) {
//                System.out.println("Username is null.");
                return null;
            }

            EntityManager entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();
            // read password hash and salt from db
            RegUserManager regUserManager = new RegUserManager(entityManager);
            RegUser user;
            try {
                user = regUserManager.findByEmail(username);
            } catch (Exception ex) {
                user = null;
                logger.error(ex.getMessage());
            }

            if (user == null) {
//                System.out.println("No account found for user [" + username + "]");
                return null;
            }

            // return salted credentials
            SaltedAuthenticationInfo info = new SaltedAuthenticationHandler(username, user.getShiropassword(), user.getShirosalt());

            return info;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }
}
