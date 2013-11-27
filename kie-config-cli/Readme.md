kie-config-cli
=======================
provides capabilities to manage system repo from command line.

Modes
---------------------------
* online (default and recommended) - on startup connects to git repository using GIT server provided by kie-wb
        all changes are made locally and published to upstream only when
        * push-changes command was explicitly executed
        * exit command will publish all local changes - to discard local changes on exit 'discard' command shall be used
* offline (sort of installer style) - creates and manipulates system repository directly on the server (no discard option)

Available commands
---------------------------

* exit - publishes any work, cleans up temporary directories and quits this command line tool
* discard - won't publishes local changes, cleans up temporary directories and quits this command line tool
* help - prints this message
* list-repo - list available repositories
* list-org-units - list available organizational units
* list-deployment - list available deployments
* create-org-unit - creates new organizational unit
* remove-org-unit - remove existing organizational unit
* add-deployment - add new deployment unit
* remove-deployment - remove existing deployment
* create-repo - creates new git repository
* remove-repo - remove existing repository from config only
* add-repo-org-unit - add repository to the organizational unit
* remove-repo-org-unit - remove repository from the organizational unit
* add-role-repo - add role(s) to repository
* remove-role-repo - remove role(s) from repository
* add-role-org-unit - add role(s) to organizational unit
* remove-role-org-unit - remove role(s) from organizational unit
* add-role-project - add role(s) to project
* remove-role-project - remove role(s) from project
* push-changes - pushes changes to upstream repository (only online mode)


How to use it
-------------------------------------
after it is successfully built/downloaded there will be distribution package available: kie-config-cli-${version}-dist.zip
it's enough to unzip it and execute kie-config-cli.sh script. By default it will start it in online mode so it will ask
for the location of ssh url it shall connect to - be default it's ssh://localhost:8001/system. To connect to remote server replace
host and port with appropriate values e.g. ssh://kie-wb-host:8001/system. It requires authentication and by default uses
user account name that the cli tool is executed with. To specify different user add it to the URL string e.g.
ssh://myuser@kie-wb-host:8001/system. It will ask for password as well to fulfill security needs for push operation.
To start online mode use:
./kie-config-cli.sh

In case cli should operate in offline mode append offline parameter to the ./kie-config-cli.sh command. It will then slightly
change behavior and will ask for the folder where .niogit (with system repo) is, so it can access the right one that requires
 modification. If there is no .niogit yet available leaving it empty will allow to create brand new setup.
To start offline mode use:
./kie-config-cli.sh offline
