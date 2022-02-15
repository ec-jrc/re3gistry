/*
 * /*
 *  * Copyright 2007,2016 EUROPEAN UNION
 *  * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by
 *  * the European Commission - subsequent versions of the EUPL (the "Licence");
 *  * You may not use this work except in compliance with the Licence.
 *  * You may obtain a copy of the Licence at:
 *  *
 *  * https://ec.europa.eu/isa2/solutions/european-union-public-licence-eupl_en
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the Licence is distributed on an "AS IS" basis,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the Licence for the specific language governing permissions and
 *  * limitations under the Licence.
 *  *
 *  * Date: 2020/05/11
 *  * Authors:
 *  * European Commission, Joint Research Centre - jrc-inspire-support@ec.europa.eu
 *  * National Land Survey of Finland, SDI Services - inspire@nls.fi
 *  *
 *  * This work was supported by the Interoperability solutions for public
 *  * administrations, businesses and citizens programme (http://ec.europa.eu/isa2)
 *  * through Action 2016.10: European Location Interoperability Solutions for e-Government (ELISE)
 *  * for e-Government (ELISE)
 */
package eu.europa.ec.re3gistry2.restapi.cache;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import eu.europa.ec.re3gistry2.javaapi.cache.ItemCache;
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;

public class CaffeineCache implements ItemCache {

    private final Set<String> languages;
    private final Cache<String, Item> itemsByUuid;
    private final Cache<String, Item> itemsByURL;

    public CaffeineCache() {
        this.languages = new HashSet<>();
        this.itemsByUuid = Caffeine.newBuilder()
                .maximumSize(10000)
                .build();
        this.itemsByURL = Caffeine.newBuilder()
                .maximumSize(10000)
                .build();
    }

    @Override
    public Set<String> getLanguages() {
        return Collections.unmodifiableSet(languages);
    }

    @Override
    public Item getByUuid(String language, String uuid) {
        return itemsByUuid.getIfPresent(getCacheKey(language, uuid));
    }

    @Override
    public Item getByUrl(String language, String url) {
        return itemsByURL.getIfPresent(getCacheKey(language, url));
    }

    @Override
    public void add(String language, Item item) {
        languages.add(language);
        itemsByUuid.put(getCacheKey(language, item.getUuid()), item);
        itemsByURL.put(getCacheKey(language, item.getUri()), item);
    }

    @Override
    public void remove(String language, String uuid) {
        Item item = itemsByUuid.getIfPresent(getCacheKey(language, uuid));
        if (item != null) {
            itemsByUuid.invalidate(getCacheKey(language, uuid));
            itemsByURL.invalidate(getCacheKey(language, item.getUri()));
        }
    }

    private String getCacheKey(String language, String uuidOrUrl) {
        return language + "_" + uuidOrUrl;
    }

    @Override
    public void removeAll() {
        languages.clear();
        itemsByUuid.invalidateAll();
        itemsByURL.invalidateAll();
    }

}
