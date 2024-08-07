/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.dmn.api;

import java.util.Map;

import org.flowable.common.engine.api.lock.LockManager;
import org.flowable.common.engine.api.management.TableMetaData;
import org.flowable.common.engine.api.management.TablePageQuery;
import org.flowable.common.engine.api.tenant.ChangeTenantIdBuilder;

/**
 * Service for admin and maintenance operations on the dmn engine.
 * 
 * These operations will typically not be used in a workflow driven application, but are used in for example the operational console.
 * 
 * @author Tijs Rademakers
 */
public interface DmnManagementService {

    /**
     * Get the mapping containing {table name, row count} entries of the database schema.
     */
    Map<String, Long> getTableCount();

    /**
     * Gets the table name (including any configured prefix) for an entity.
     */
    String getTableName(Class<?> idmEntityClass);

    /**
     * Gets the metadata (column names, column types, etc.) of a certain table. Returns null when no table exists with the given name.
     */
    TableMetaData getTableMetaData(String tableName);

    /**
     * Creates a {@link TablePageQuery} that can be used to fetch {@link org.flowable.common.engine.api.management.TablePage} containing specific sections of table row data.
     */
    TablePageQuery createTablePageQuery();

    /**
     * Create a {@link ChangeTenantIdBuilder} that can be used to change the tenant id of the Historic Decision instances.
     * <p>
     * You must provide the source tenant id and the destination tenant id. 
     */
    ChangeTenantIdBuilder createChangeTenantIdBuilder(String fromTenantId, String toTenantId);
    
    /**
     * Acquire a lock manager for the requested lock.
     * This is a stateless call, this means that every time a lock manager
     * is requested a new one would be created. Make sure that you release the lock
     * once you are done.
     *
     * @param lockName the name of the lock that is being requested
     *
     * @return the lock manager for the given lock
     */
    LockManager getLockManager(String lockName);
}
