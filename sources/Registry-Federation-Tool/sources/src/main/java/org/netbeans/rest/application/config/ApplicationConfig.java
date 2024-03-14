/*
 * Copyright 2010,2015 EUROPEAN UNION
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * Date: __/__/____
 * Authors: European Commission, Joint Research Centre
 * inspire-registry-dev@jrc.ec.europa.eu
 *
 * This work was supported by the EU  Interoperability Solutions for
 * European Public Administrations Programme (http://ec.europa.eu/isa)
 * through Action 1.17: Re-usable INSPIRE Reference Platform 
 */
package org.netbeans.rest.application.config;

import java.util.Set;
import javax.ws.rs.core.Application;

@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(eu.europa.ec.ror.restful.Registers.class);
        resources.add(eu.europa.ec.ror.restful.Registries.class);
        resources.add(eu.europa.ec.ror.restful.Relations.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.ClassNotFoundExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.ConversionExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.DatabaseExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.EntityExistsExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.EntityNotFoundExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.IOExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.IllegalAccessExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.IllegalArgumentExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.IllegalStateExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.InvocationTargetExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.JAXBExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.JPARSConfigurationExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.JPARSExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.MalformedURLExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.NamingExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.NoResultExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.NoSuchMethodExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.NonUniqueResultExceptionExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.OptimisticLockExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.PersistenceExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.PessimisticLockExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.QueryTimeoutExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.RollbackExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.TransactionRequiredExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.UnsupportedMediaTypeExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.resources.EntityResource.class);
        resources.add(org.eclipse.persistence.jpa.rs.resources.PersistenceResource.class);
        resources.add(org.eclipse.persistence.jpa.rs.resources.PersistenceUnitResource.class);
        resources.add(org.eclipse.persistence.jpa.rs.resources.QueryResource.class);
        resources.add(org.eclipse.persistence.jpa.rs.resources.SingleResultQueryResource.class);
        resources.add(org.eclipse.persistence.jpa.rs.resources.unversioned.EntityResource.class);
        resources.add(org.eclipse.persistence.jpa.rs.resources.unversioned.PersistenceResource.class);
        resources.add(org.eclipse.persistence.jpa.rs.resources.unversioned.PersistenceUnitResource.class);
        resources.add(org.eclipse.persistence.jpa.rs.resources.unversioned.QueryResource.class);
        resources.add(org.eclipse.persistence.jpa.rs.resources.unversioned.SingleResultQueryResource.class);
    }
    
}