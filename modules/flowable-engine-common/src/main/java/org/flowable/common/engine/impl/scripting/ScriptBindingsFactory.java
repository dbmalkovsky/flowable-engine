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

package org.flowable.common.engine.impl.scripting;

import java.util.ArrayList;
import java.util.List;

import org.flowable.common.engine.impl.AbstractEngineConfiguration;

/**
 * @author Tom Baeyens
 * @author Joram Barrez
 */
public class ScriptBindingsFactory {

    protected AbstractEngineConfiguration engineConfiguration;
    protected List<ResolverFactory> resolverFactories;

    public ScriptBindingsFactory(AbstractEngineConfiguration engineConfiguration, List<ResolverFactory> resolverFactories) {
        this.engineConfiguration = engineConfiguration;
        this.resolverFactories = resolverFactories;
    }

    public Resolver createResolver(ScriptEngineRequest request) {
        List<Resolver> scriptResolvers = new ArrayList<>();
        scriptResolvers.addAll(request.getAdditionalResolvers());
        for (ResolverFactory scriptResolverFactory : resolverFactories) {
            Resolver resolver = scriptResolverFactory.createResolver(engineConfiguration, request.getScopeContainer(), request.getInputVariableContainer());
            if (resolver != null) {
                scriptResolvers.add(resolver);
            }
        }
        return new CompositeResolver(scriptResolvers);
    }

    public List<ResolverFactory> getResolverFactories() {
        return resolverFactories;
    }

    public void setResolverFactories(List<ResolverFactory> resolverFactories) {
        this.resolverFactories = resolverFactories;
    }
}
