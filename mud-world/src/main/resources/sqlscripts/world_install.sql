create role mudengine_world_app login;
alter role mudengine_world_app with password 'mudengine_world_app';

create role mudengine_world CREATEDB;
create schema mudengine_world authorization mudengine_world;
grant mudengine_world to mudengine_world_app;

alter role mudengine_world_app set search_path to mudengine_world;
