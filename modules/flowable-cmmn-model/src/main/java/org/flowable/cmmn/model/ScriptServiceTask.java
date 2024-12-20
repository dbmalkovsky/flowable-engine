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
package org.flowable.cmmn.model;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Dennis
 */
public class ScriptServiceTask extends ServiceTask {

    public static final String SCRIPT_TASK = "script";

    protected boolean autoStoreVariables;

    public ScriptServiceTask() {
        this.type = SCRIPT_TASK;
    }

    public String getScriptFormat() {
        return implementationType;
    }

    public void setScriptFormat(String scriptFormat) {
        this.implementationType = scriptFormat;
    }

    public String getScript() {
        for (FieldExtension fieldExtension : fieldExtensions) {
            if ("script".equalsIgnoreCase(fieldExtension.getFieldName())) {
                String script = fieldExtension.getStringValue();
                if (StringUtils.isNotEmpty(script)) {
                    return script;
                }
                return fieldExtension.getExpression();
            }
        }
        return null;
    }

    public boolean isAutoStoreVariables() {
        return autoStoreVariables;
    }

    public void setAutoStoreVariables(boolean autoStoreVariables) {
        this.autoStoreVariables = autoStoreVariables;
    }

}
