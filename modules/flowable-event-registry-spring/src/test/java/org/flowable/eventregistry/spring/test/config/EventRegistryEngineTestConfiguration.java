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
package org.flowable.eventregistry.spring.test.config;

import java.util.function.Consumer;

import javax.sql.DataSource;

import org.flowable.eventregistry.api.ChannelModelProcessor;
import org.flowable.eventregistry.api.EventRegistry;
import org.flowable.eventregistry.api.EventRepositoryService;
import org.flowable.eventregistry.impl.EventRegistryEngine;
import org.flowable.eventregistry.spring.EventRegistryFactoryBean;
import org.flowable.eventregistry.spring.SpringEventRegistryEngineConfiguration;
import org.flowable.idm.spring.configurator.SpringIdmEngineConfigurator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

/**
 * @author Filip Hrisafov
 */
@Configuration(proxyBeanMethods = false)
public class EventRegistryEngineTestConfiguration {

    @Bean
    public DataSource dataSource(
        @Value("${jdbc.url:jdbc:h2:mem:flowable-spring-jms-test;DB_CLOSE_DELAY=1000}") String jdbcUrl,
        @Value("${jdbc.driver:org.h2.Driver}") String jdbcDriverClass,
        @Value("${jdbc.username:sa}") String jdbcUsername,
        @Value("${jdbc.password:}") String jdbcPassword
    ) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setMinimumIdle(0);
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setDriverClassName(jdbcDriverClass);
        dataSource.setUsername(jdbcUsername);
        dataSource.setPassword(jdbcPassword);
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public SpringEventRegistryEngineConfiguration eventRegistryEngineConfiguration(DataSource dataSource, PlatformTransactionManager transactionManager,
            SpringIdmEngineConfigurator springIdmEngineConfigurator,
            ObjectProvider<ChannelModelProcessor> channelDefinitionProcessors,
            ObjectProvider<Consumer<SpringEventRegistryEngineConfiguration>> configurationConfigurers) {
        
        SpringEventRegistryEngineConfiguration engineConfiguration = new SpringEventRegistryEngineConfiguration();
        engineConfiguration.setDataSource(dataSource);
        engineConfiguration.setTransactionManager(transactionManager);
        engineConfiguration.setDatabaseSchemaUpdate(SpringEventRegistryEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        channelDefinitionProcessors.stream().forEach(engineConfiguration::addChannelModelProcessor);

        engineConfiguration.setIdmEngineConfigurator(springIdmEngineConfigurator);

        configurationConfigurers.orderedStream()
                .forEach(consumer -> consumer.accept(engineConfiguration));

        return engineConfiguration;
    }

    @Bean(name = "springIdmEngineConfigurator")
    public SpringIdmEngineConfigurator springIdmEngineConfigurator() {
        return new SpringIdmEngineConfigurator();
    }


    @Bean("eventRegistryEngine")
    public EventRegistryFactoryBean eventRegistryFactoryBean(SpringEventRegistryEngineConfiguration eventRegistryEngineConfiguration) {
        EventRegistryFactoryBean factoryBean = new EventRegistryFactoryBean();
        factoryBean.setEventEngineConfiguration(eventRegistryEngineConfiguration);
        return factoryBean;
    }

    @Bean
    public EventRegistry eventRegistry(EventRegistryEngine eventRegistryEngine) {
        return eventRegistryEngine.getEventRegistry();
    }

    @Bean
    public EventRepositoryService eventRepositoryService(EventRegistryEngine eventRegistryEngine) {
        return eventRegistryEngine.getEventRepositoryService();
    }

    @Bean
    public CustomPropertiesBean customPropertiesBean() {
        return new CustomPropertiesBean();
    }

}
