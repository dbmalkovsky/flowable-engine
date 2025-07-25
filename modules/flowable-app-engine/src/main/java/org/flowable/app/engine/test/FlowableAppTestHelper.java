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
package org.flowable.app.engine.test;

import org.flowable.app.engine.AppEngine;

/**
 * A helper for the Flowable {@link FlowableAppExtension} that can be used within the JUnit Jupiter context store.
 *
 * @author Filip Hrisafov
 */
public class FlowableAppTestHelper {

    protected final AppEngine appEngine;
    protected String deploymentIdFromDeploymentAnnotation;

    public FlowableAppTestHelper(AppEngine appEngine) {
        this.appEngine = appEngine;
    }

    public AppEngine getAppEngine() {
        return appEngine;
    }

    public String getDeploymentIdFromDeploymentAnnotation() {
        return deploymentIdFromDeploymentAnnotation;
    }

    public void setDeploymentIdFromDeploymentAnnotation(String deploymentIdFromDeploymentAnnotation) {
        this.deploymentIdFromDeploymentAnnotation = deploymentIdFromDeploymentAnnotation;
    }
}
