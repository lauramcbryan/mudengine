create role mudengine_command_app login;
alter role mudengine_command_app with password 'mudengine_command_app';

create role mudengine_command CREATEDB;
create schema mudengine_command authorization mudengine_command;
grant mudengine_command to mudengine_command_app;

alter role mudengine_command_app set search_path to mudengine_command;
