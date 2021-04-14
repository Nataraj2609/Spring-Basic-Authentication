# Spring-Basic-Authentication
Complete Solution of Spring Security - Basic authentication - Fully guarded End Points


Spring Security
	
	JWT Security - 

		https://erinc.io/blog/

		https://www.freecodecamp.org/news/how-to-setup-jwt-authorization-and-authentication-in-spring/

Basic Authentication - Full working module

		https://www.marcobehler.com/guides/spring-security	


Spring Security is really just a bunch of servlet filters that help you add authentication and authorization to your web application.

Before you become a Spring Security Guru, you need to understand three important concepts:

		1. Authentication - username password

		2. Authorization - role

		3. Servlet Filters 

Any Spring web application is just one servlet.

Spring’s good old DispatcherServlet, that redirects incoming HTTP requests (e.g. from a browser) to your @Controllers or @RestControllers.Optimally, the authentication and authorization should be done before a request hits your @Controllers.
Luckily, there’s a way to do exactly this in the Java web world: you can put filters in front of servlets, which means you could think about writing a SecurityFilter and configure it in your Tomcat (servlet container/application server) to filter every incoming HTTP request before it hits your servlet.

		403 - Forbidden
		401 - Unauthorised

A SecurityFilter has roughly 4 tasks

		1. First, the filter needs to extract a username/password from the request. It could be via a Basic Auth HTTP Header, or form fields, or a cookie, etc.

				        //For Basic Authentication
				        String auth =  "John:999";
				        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
				        String authHeaderValue = "Basic " + new String(encodedAuth);
				        connection.setRequestProperty("Authorization", authHeaderValue);

				        Authorization   Basic am9objo5OTk=

		2. Then the filter needs to validate that username/password combination against something, like a database.

		3. The filter needs to check, after successful authentication, that the user is authorized to access the requested URI.

		4. If the request survives all these checks, then the filter can let the request go through to your DispatcherServlet, i.e. your @Controllers.

(4) If you expand that one line into a list, it looks like Spring Security does not just install one filter, instead it installs a whole filter chain consisting of 15 (!) different filters.

So, when an HTTPRequest comes in, it will go through all these 15 filters, before your request finally hits your @RestControllers. The order is important, too, starting at the top of that list and going down to the bottom.

![Image of Itachi](https://www.marcobehler.com/images/filterchain-1a.png)

Those filters, for a large part, are Spring Security. Not more, not less. They do all the work. What’s left for you is to configure how they do their work, i.e. which URLs to protect, which to ignore and what database tables to use for authentication.

Hence, we need to have a look at how to configure Spring Security, next.



How to configure Spring Security: WebSecurityConfigurerAdapter

	@EnableWebSecurity.

	Extends WebSecurityConfigurer, which basically offers you a configuration DSL/methods. With those methods, you can specify what URIs in your application to protect or what exploit protections to enable/disable.

configure method  - Where our security logics will be written

For Simple Basic auth,

			
		@Configuration
		@EnableWebSecurity
		public class SecurityConfig extends WebSecurityConfigurerAdapter {

		    @Override
		    protected void configure(HttpSecurity http) throws Exception {
		        http
		                .csrf().disable()
		                .authorizeRequests().anyRequest().authenticated()
		                .and()
		                .httpBasic();
		    }

		    @Autowired
		    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		        auth.inMemoryAuthentication()
		                .withUser("user").password(passwordEncoder().encode("user"))
		                .authorities("ROLE_USER");
		    }

		    @Bean
		    public PasswordEncoder passwordEncoder() {
		        	return new BCryptPasswordEncoder();
		    	}
			}

In Order to permit all users to a route

	  http.authorizeRequests()
          .antMatchers("/vital/*").permitAll()
          .authorizeRequests().anyRequest().authenticated()
          .and()
          .httpBasic();

In Order to get Spring Boot default login page

.and()
     .formLogin().permitAll()
	 .and()
	 .httpBasic();


My Way:

	1. Filter - Needs to extract Username and Password from our request

			BasicAuthenticationFilter: Tries to find a Basic Auth HTTP Header on the request and if found, tries to authenticate the user with the header’s username and password.

			UsernamePasswordAuthenticationFilter: Tries to find a username/password request parameter/POST body and if found, tries to authenticate the user with those values.

			DefaultLoginPageGeneratingFilter: Generates a login page for you, if you don’t explicitly disable that feature. THIS filter is why you get a default login page when enabling Spring Security.

			DefaultLogoutPageGeneratingFilter: Generates a logout page for you, if you don’t explicitly disable that feature.

			FilterSecurityInterceptor: Does your authorization.

  
    Here, I have choose DefaultLoginPageGeneratingFilter which run behind the scenes

    2. Authentication:

    			In-memory auth is used as above.

    			1. Creating a specific DB for it[using this]
    			2. OAuth2 

    	Note: Depending on your scenario, you need to specify different @Beans to get Spring Security working, otherwise you’ll end up getting pretty confusing exceptions (like a NullPointerException if you forgot to specify the PasswordEncoder). Keep that in mind.

    	create table users (id int auto_increment primary key, username varchar(255), password varchar(255));
    	nat -- p@ss ($2y$12$c0Z9uuY7dcLBCtnDYeAVquE2avzdA8PY5opJIbXkaZcPtd8J8n5Dy)

    	In this case Spring Security needs you to define two beans to get authentication up and running.

					A UserDetailsService.

					A PasswordEncoder.

		If you want to, say, use the BCrypt password hashing function (Spring Security’s default) for all your passwords, you would specify this @Bean in your SecurityConfig.

		SHA1, SHA256, and SHA512 are all fast hashes and are bad for passwords. SCRYPT and BCRYPT are both a slow hash and are good for passwords. ... However, for this instance it is not recommended that regular web applications use any member of the SHA family, be it SHA1, SHA256, or even SHA512 for password hashing.

			@Bean
		    public PasswordEncoder passwordEncoder() {
		        return new BCryptPasswordEncoder();
		    }

		    @Bean
		    public UserDetailsService userDetailsService(){
		        return new MyDatabaseUserDetailService();
		    }

		Create a class MyDatabaseUserDetailsService under service package

			public class MyDatabaseUserDetailsService implements UserDetailsService {

			    @Autowired
			    private UserRepository userRepository;

			    @Override
			    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
			        User user = userRepository.findByUsername(username);
			        if (user == null) {
			            throw new UsernameNotFoundException(username);
			        }
			        return new MyUserPrincipal(user);
			    }
			}

		Then You need to add One more class that implements UserDetails

					public class MyUserPrincipal implements UserDetails {
				    private User user;

				    public MyUserPrincipal(User user) {
				        this.user = user;
				    }

				     @Override
				    public Collection<? extends GrantedAuthority> getAuthorities() {
				        return null;
				    }

				    @Override
				    public String getPassword() {
				        return user.getPassword();
				    }

				    @Override
				    public String getUsername() {
				        return user.getUsername();
				    }

				    @Override
				    public boolean isAccountNonExpired() {
				        return true;
				    }

				    @Override
				    public boolean isAccountNonLocked() {
				        return true;
				    }

				    @Override
				    public boolean isCredentialsNonExpired() {
				        return true;
				    }

				    @Override
				    public boolean isEnabled() {
				        return true;
				    }

		Then
			we need an Entity, Repository and load them in above class by autowiring userRepository
			That's all

-----------------------------------------------------------------------------------------------------------
b)
  
	  isAccountNonExpired
	  isAccountNonLocked
	  isCredentialsNonExpired
	  isEnabled

	  Change Db Table structure to possess this fields


		alter table users add column(isAccountNonExpired boolean, isAccountNonLocked boolean, isCredentialsNonExpired boolean, isEnabled boolean);
		update users set isAccountNonExpired=true , isAccountNonLocked=true, isCredentialsNonExpired=true, isEnabled=true;

	==> MySQL does not have built-in Boolean type. However, it uses TINYINT(1) instead. To make it more convenient, MySQL provides BOOLEAN or BOOL as the synonym of TINYINT(1).

		    @Column(nullable = false, columnDefinition = "TINYINT(1)")
	    	private boolean isAccountNonExpired;

-----------------------------------------------------------------------------------------------------------
  3.Authorization
  		Adding Roles:

  		What are Authorities? What are Roles?
		

		An authority (in its simplest form) is just a string, it can be anything like: user, ADMIN, ROLE_ADMIN or 53cr37_r0l3.

		A role is an authority with a ROLE_ prefix. So a role called ADMIN is the same as an authority called ROLE_ADMIN.

				alter table users add column(authorities varchar(255));
				update users set authorities='ROLE_USER' where id=2;  //Another one is ROLE_ADMIN

		The only thing that’s left to do is to adjust your UserDetailsService to read in that authorities column.

		Change in Entity as   
							@Column(nullable = false)
    						private List<String> authorities;

    	   @Override
		    public Collection<? extends GrantedAuthority> getAuthorities() {
		        List<String> userList = new ArrayList<>();
		        userList.add(user.getAuthorities());
		        List<SimpleGrantedAuthority> grantedAuthorities = userList.stream().map(authority -> new SimpleGrantedAuthority(authority)).collect(Collectors.toList());
		        System.out.println(grantedAuthorities);
		        return grantedAuthorities;
		    }

		Restrict Via

	                .antMatchers("/pat").hasRole("ADMIN")

	    Login http://localhost:8080/pat using both users having ROLE - ADMIN & User 
	    For User  - 	There was an unexpected error (type=Forbidden, status=403).
	    For Admin -     Access will be given


 
