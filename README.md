# MUDEngine

MUDEngine it's an engine for a Multi User Dungeon written over Spring Boot REST services
under a microservice architecture.

## Architecture


This project is encomprised to 4 different layers:


### Worker layer

Here we have the basic entities of the game.  Each of of them resides in a
separate project with no iteraction between them.  (Well, some iteraction at
the moment, but I'll find a way to further isolate them).
Each entity includes a metadata service with the suffix "-class".  That
service is used to retrieve information used to create new entities and to
tag properties that are common to all instances of the particular entity.

The services are:

[Place](mud-world): a place in the game world.  Each place can depict a large space (like
a forest) or a small place, like a tunnel.  Places can contain items, beings
and exits.

[Item](mud-item): an item in the game.  Items can be used by beings to increase their
statistics and abilities.  Some items may be consumed to create structures
or other items.

[Being](mud-being): the central piece of the game.  Beings are self-aware entities that
wander through the game world.  Beings can iteract with items and navigate
through places, executing actions.  Beings can be controlled by an human
or by the A.I.



### Orchestration layer

This layer responds to player's needies.  It encomprises

[Action](mud-action): rules engine that receives player commands, process the actions and
triggers outcomes to services in worker layer.  In this service resides the
action runtime and the turns counter.

[Player](mud-player): administrative service where humans can register themselves and perform
other mundane tasks like password reset and profile edit.
In this service the player triggers a being creation that will be his avatar
in the game world.

[Message](mud-message): notification service to store the events that must be propagated to
the player.  Each message is bound to a being and is stored until consumed.
That service is built with multi language support, according to player's
language settings.


### Interface layer

This layer contain the service gateway the unify and regulates the clients
access to inner services.  In some platforms (like AWS) this goal is achieved
by use of already available tools, like AWS API Gateway.  Where these resources
aren't available, a Zuul implemented proxy ([MudApi](mud-api)) serves as gateway.


### Client layer

This layer is encomprised by a unique java client project ([MudClient](mud-client)).
This project is here just for demonstration purposes in order to show up how a 
player interface can iteract with the service gateway.
Implemented as a telnet server, it retains a 80's retro visual, just like the
text adventures of that epoch.


### Support projects

These other projects contains shared classes and scripts to develop and deploy
the solution.


[Mud-Common](mud-common): service signatures and shared security support code used by all
the services.

[Mud-Common-Client](mud-common-client): support project with the classes returned by all services.
No Spring Boot, no cloud dependency, just Plain Old Java Objects.
(Used by MudClient)

[Mud-Infra](mud-infra): simple project used for local launch. Docker files to create and populate
the Consul and the Vault images are also available.


## Running

In order to run this solution:

- Build all the projects using the parent [pom.xml](mud-infra/app/pom.xml);

- Run the infra [composite](mud-infra/docker-compose.yml).
The composite raise and populate his own with Consul, Vault and PostgreSQL services.  
It will take some time to run, wait for container activity to calm down before proceed;
(you can run this composite in another machine if your memory restrictions are severe)

- Update the [.env file](mud-infra/app/.env)  to point to the server where the infra composite
is running.  Doesn't use localhost or 127.0.0.1, even if the infra composite is running in 
the same machine (in these cases, use the machine IP instead);

- Run the app [composite](mud-infra/app/docker-compose.yml);
Same as before, allow some time for the apps finish starting;

- Run the [Mud-Client](mud-client) project;
(as this is technically an external project, it's not included in mudengine docker compose file)

- Connect to the telnet service at 9876 port;

At this time, we only have the administrative services working.  You can register your account,
activate it and create beings for your player.  The command engine will come in the next release.


## Goals and Motivation

This project was designed with some goals in mind.

The first of them is to be a laboratory to study and try new technologies 
specially regarding microservice and cloud technologies.
Here I'm not afraid to kick things out if I feel like they aren't contributing
for the project in a manner that pays off the trouble they caused.

The second goal, and that's because we must have some fun, is to provide an
implementation of a Multi User Dungeon (MUD) engine that could be adapted to
different world games and scenarios.  With a rules engine that work based on
predicates (pretty much like lambdas, but coded in SpEl for now), the 
objective is to present a platform from where the administrator can create a 
brand new game world just through database initial loading.

That being said, please note that:

- THIS ISN'T A HELLO WORLD PROJECT.
Here we have some serious effort put on architecture, design and implementation
for the (currently) 10 services distributed among 4 layers and 9 projects.  
Database was modeled externally before the JPA entities, because I trust that's
the way to optimize data storage and access strategy.  Each service runs under 
an isolated schema that can be implemented in Postgre, mySQL, mongoDB, you name
it.


- THIS ISN'T A FINISHED WORK.
And probably never will be.  There's a long list of new things I wanna try here
before I can move them for real-life projects.  So, for safe code, stay in the
master branch (or develop for some finished features) and avoid the inner
depths of my mind (called the feature branches).  There be dragons.

- I'M NOT A PREACHER of Java, Spring Boot, Docker or any particular technology.
Although I've used here a variety of tools, some of them beyond necessity, on
my work (and in my life) I believe that technology is just a way that humans,
business and corporations created to achieve their dreams.  And more: put them
in a tangible matter.
