create role mudengine_being_app login;
alter role mudengine_being_app with password 'mudengine_being_app';

create role mudengine_being CREATEDB;
create schema mudengine_being authorization mudengine_being;
grant mudengine_being to mudengine_being_app;

alter role mudengine_being_app set search_path to mudengine_being;