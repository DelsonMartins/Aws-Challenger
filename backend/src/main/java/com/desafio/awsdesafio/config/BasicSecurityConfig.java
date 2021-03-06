package com.desafio.awsdesafio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Profile("basic-security")
@Configuration
@EnableWebSecurity
public class BasicSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("admin").password("{noop}admin").roles("ROLE");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/aws/file/all").permitAll() // qualquer um acessa
				.antMatchers("/h2-console").permitAll()  // qualquer um acessa
				.anyRequest().authenticated() //Precisa estar autenticado para qualquer requisição
				.and().httpBasic() //Tipo de autenticação
				.and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //desabilitando  a criação de sessão. Sem estado!
				.and()
				.csrf().disable(); //desabilita o suporte a cross-site request forgery.
	}
	
}