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
package eu.europa.ec.re3gistry2.javaapi.cache;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.base.utility.WebConstants;
import eu.europa.ec.re3gistry2.javaapi.cache.ItemCache;
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;

public class EhCache implements ItemCache {

    private final Set<String> languages;
    private final Object sync;

    public EhCache() {
        this.languages = new HashSet<>();
        this.sync = new Object();
    }

    @Override
    public Set<String> getLanguages() {
        return Collections.unmodifiableSet(languages);
    }

    @Override
    public Item getByUuid(String language, String uuid) {
        synchronized (sync) {
            // Get configuration properties
            final Properties properties = Configuration.getInstance().getProperties();
            String cachePath = properties.getProperty(BaseConstants.KEY_DISK_CACHE_PATH, null);
            String cacheMaximumDiskSpace = properties.getProperty(BaseConstants.KEY_DISK_CACHE_MAXIMUM_SPACE, "5");
            int cacheMaximumGig;
            try {
                cacheMaximumGig = Integer.parseInt(cacheMaximumDiskSpace);
            } catch (Exception e) {
                cacheMaximumGig = 5;
            }

            if (cachePath != null && !cachePath.trim().equals("")) {
                PersistentCacheManager persistentCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                        .with(CacheManagerBuilder.persistence(cachePath))
                        .withCache(BaseConstants.KEY_CACHE_NAME_UUID, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Item.class,
                                ResourcePoolsBuilder.newResourcePoolsBuilder().disk(cacheMaximumGig, MemoryUnit.GB, true))
                        ).build(true);

                Item it = persistentCacheManager.getCache(BaseConstants.KEY_CACHE_NAME_UUID, String.class, Item.class).get(getCacheKey(language, uuid));
                persistentCacheManager.close();

                return it;
            } else {
                return null;
            }
        }
    }

    @Override
    public Item getByUrl(String language, String url, Integer version) {
        synchronized (sync) {
            // Get configuration properties
            final Properties properties = Configuration.getInstance().getProperties();
            String cachePath = properties.getProperty(BaseConstants.KEY_DISK_CACHE_PATH, null);
            String cacheMaximumDiskSpace = properties.getProperty(BaseConstants.KEY_DISK_CACHE_MAXIMUM_SPACE, "5");
            int cacheMaximumGig;
            try {
                cacheMaximumGig = Integer.parseInt(cacheMaximumDiskSpace);
            } catch (Exception e) {
                cacheMaximumGig = 5;
            }

            if (cachePath != null && !cachePath.trim().equals("")) {
                PersistentCacheManager persistentCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                        .with(CacheManagerBuilder.persistence(cachePath))
                        .withCache(BaseConstants.KEY_CACHE_NAME_URL, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Item.class,
                                ResourcePoolsBuilder.newResourcePoolsBuilder().disk(cacheMaximumGig, MemoryUnit.GB, true))
                        ).build(true);

                Item it = persistentCacheManager.getCache(BaseConstants.KEY_CACHE_NAME_URL, String.class, Item.class).get(version != null ? getCacheKey(language, url + ":" + version) : getCacheKey(language, url));
                persistentCacheManager.close();

                return it;
            } else {
                return null;
            }
        }
    }

    @Override
    public void add(String language, Item item, Integer version) {
        synchronized (sync) {
            // Get configuration properties
            final Properties properties = Configuration.getInstance().getProperties();
            String cachePath = properties.getProperty(BaseConstants.KEY_DISK_CACHE_PATH, null);
            String cacheMaximumDiskSpace = properties.getProperty(BaseConstants.KEY_DISK_CACHE_MAXIMUM_SPACE, "5");
            int cacheMaximumGig;
            try {
                cacheMaximumGig = Integer.parseInt(cacheMaximumDiskSpace);
            } catch (Exception e) {
                cacheMaximumGig = 5;
            }

            if (cachePath != null && !cachePath.trim().equals("")) {
                PersistentCacheManager persistentCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                        .with(CacheManagerBuilder.persistence(cachePath))
                        .withCache(BaseConstants.KEY_CACHE_NAME_UUID, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Item.class,
                                ResourcePoolsBuilder.newResourcePoolsBuilder().disk(cacheMaximumGig, MemoryUnit.GB, true))
                        ).withCache(BaseConstants.KEY_CACHE_NAME_URL, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Item.class,
                                ResourcePoolsBuilder.newResourcePoolsBuilder().disk(cacheMaximumGig, MemoryUnit.GB, true))
                        ).build(true);

                persistentCacheManager.getCache(BaseConstants.KEY_CACHE_NAME_UUID, String.class, Item.class).put(getCacheKey(language, item.getUuid()), item);
                persistentCacheManager.getCache(BaseConstants.KEY_CACHE_NAME_URL, String.class, Item.class).put(version != null ? getCacheKey(language, item.getVersion().getUri()) : getCacheKey(language, item.getUri()), item);
                persistentCacheManager.close();

                languages.add(language);
            }
        }
    }

    @Override
    public void remove(String language, String uuid) {
        synchronized (sync) {
            // Get configuration properties
            final Properties properties = Configuration.getInstance().getProperties();
            String cachePath = properties.getProperty(BaseConstants.KEY_DISK_CACHE_PATH);
            String cacheMaximumDiskSpace = properties.getProperty(BaseConstants.KEY_DISK_CACHE_MAXIMUM_SPACE, "5");

            int cacheMaximumGig;
            try {
                cacheMaximumGig = Integer.parseInt(cacheMaximumDiskSpace);
            } catch (Exception e) {
                cacheMaximumGig = 5;
            }

            if (cachePath != null && !cachePath.trim().equals("")) {
                PersistentCacheManager persistentCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                        .with(CacheManagerBuilder.persistence(cachePath))
                        .withCache(BaseConstants.KEY_CACHE_NAME_UUID, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Item.class,
                                ResourcePoolsBuilder.newResourcePoolsBuilder().disk(cacheMaximumGig, MemoryUnit.GB, true))
                        ).withCache(BaseConstants.KEY_CACHE_NAME_URL, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Item.class,
                                ResourcePoolsBuilder.newResourcePoolsBuilder().disk(cacheMaximumGig, MemoryUnit.GB, true))
                        ).build(true);

                Item item = persistentCacheManager.getCache(BaseConstants.KEY_CACHE_NAME_UUID, String.class, Item.class).get(getCacheKey(language, uuid));
                if (item != null) {
                    persistentCacheManager.getCache(BaseConstants.KEY_CACHE_NAME_UUID, String.class, Item.class).remove(getCacheKey(language, uuid));
                    persistentCacheManager.getCache(BaseConstants.KEY_CACHE_NAME_URL, String.class, Item.class).remove(getCacheKey(language, item.getUri()));
                }

                persistentCacheManager.close();
            }
        }
    }

    @Override
    public void removeAll() {
        languages.clear();

        synchronized (sync) {
            // Get configuration properties
            final Properties properties = Configuration.getInstance().getProperties();
            String cachePath = properties.getProperty(BaseConstants.KEY_DISK_CACHE_PATH);
            String cacheMaximumDiskSpace = properties.getProperty(BaseConstants.KEY_DISK_CACHE_MAXIMUM_SPACE, "5");

            int cacheMaximumGig;
            try {
                cacheMaximumGig = Integer.parseInt(cacheMaximumDiskSpace);
            } catch (Exception e) {
                cacheMaximumGig = 5;
            }

            if (cachePath != null && !cachePath.trim().equals("")) {
                PersistentCacheManager persistentCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                        .with(CacheManagerBuilder.persistence(cachePath))
                        .withCache(BaseConstants.KEY_CACHE_NAME_UUID, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Item.class,
                                ResourcePoolsBuilder.newResourcePoolsBuilder().disk(cacheMaximumGig, MemoryUnit.GB, true))
                        ).withCache(BaseConstants.KEY_CACHE_NAME_URL, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Item.class,
                                ResourcePoolsBuilder.newResourcePoolsBuilder().disk(cacheMaximumGig, MemoryUnit.GB, true))
                        ).build(true);

                persistentCacheManager.getCache(BaseConstants.KEY_CACHE_NAME_UUID, String.class, Item.class).clear();
                persistentCacheManager.getCache(BaseConstants.KEY_CACHE_NAME_URL, String.class, Item.class).clear();
                persistentCacheManager.close();
            }
        }
    }

    private String getCacheKey(String language, String uuidOrUrl) {
        return language + "_" + uuidOrUrl;
    }
}
