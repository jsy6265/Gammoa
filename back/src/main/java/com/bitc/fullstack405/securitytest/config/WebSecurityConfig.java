package com.bitc.fullstack405.securitytest.config;


//import com.bitc.fullstack405.securitytest.service.MemberDetailsService;
import com.bitc.fullstack405.securitytest.handler.JwtLogoutSuccessHandler;
import com.bitc.fullstack405.securitytest.service.UserDetailsServiceImpl;
import com.bitc.fullstack405.securitytest.handler.JwtLogoutHandler;
import com.bitc.fullstack405.securitytest.utill.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final UserDetailsServiceImpl userDetailsService;

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return webSecurity -> webSecurity.ignoring()
        .requestMatchers(new AntPathRequestMatcher("/static/**")); // h2 콘솔 사용 안하면 얘만 살려두면 됨
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // 지정한 여러개의 url에 대해서 모든 사용 권한을 허용
            .requestMatchers("/","/api/members/**").permitAll() // 로그인, 회원가입 오픈
            .requestMatchers(HttpMethod.PUT, "/api/users/**").authenticated() // PUT 요청 인증 필요
            .anyRequest().authenticated()
        )
        .logout(logout -> logout
            .logoutUrl("/api/members/logout")
            .addLogoutHandler(jwtLogoutHandler())
            .logoutSuccessHandler(jwtLogoutSuccessHandler()))
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
  }

  @Bean
  public JwtLogoutHandler jwtLogoutHandler() {
    return new JwtLogoutHandler();
  }

  @Bean
  public JwtLogoutSuccessHandler jwtLogoutSuccessHandler(){
    return new JwtLogoutSuccessHandler();
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}












