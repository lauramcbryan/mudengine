create role mudengine_action_app login;
alter role mudengine_action_app with password 'mudengine_action_app';

create role mudengine_action CREATEDB;
create schema mudengine_action authorization mudengine_action;
grant mudengine_action to mudengine_action_app;

alter role mudengine_action_app set search_path to mudengine_action;
