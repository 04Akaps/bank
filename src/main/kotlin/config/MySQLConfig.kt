package org.example.config

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
class MySQLConfig(
    @Value("\${spring.datasource.url}") val url: String,
    @Value("\${spring.datasource.username}") val username: String,
    @Value("\${spring.datasource.password}") val password: String,
    @Value("\${spring.datasource.driver-class-name}") val driver: String,
) {

    @Bean
    fun dataSource(): DataSource {
        val dataSource = DriverManagerDataSource(url, username, password)
        dataSource.setDriverClassName(driver)
        return dataSource
    }

    @Bean
    fun transactionManager(dataSource: DataSource): DataSourceTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }

}