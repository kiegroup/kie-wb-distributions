Application can be executed in three different profiles:
1. full profile - default profile that is active without additional configuration required (UI and remote services e.g. REST)
2. execution server profile - profile that disables completely UI components of the application and allows only remote
   access e.g. via REST interface
3. ui server profile - profile that disables remote services e.g REST and allows only UI access to the application


Configuration:
To change the profile there is a two step configuration required

1. enable correct web.xml inside the application
      web.xml (default) for full profile
      web-exec-server.xml for execution server profile
      web-ui-server.xml for ui server profile

2. start application server with additional system property to instruct the profile manager to activate given profile
    -Dorg.kie.active.profile=full - to activate full profile or skip the property completely
    -Dorg.kie.active.profile=exec-server - to activate execution server profile
    -Dorg.kie.active.profile=ui-server - to activate ui server profile
