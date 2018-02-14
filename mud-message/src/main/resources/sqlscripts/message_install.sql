create role mudengine_message_app login;
alter role mudengine_message_app with password 'mudengine_message_app';

create role mudengine_message CREATEDB;
create schema mudengine_message authorization mudengine_message;
grant mudengine_message to mudengine_message_app;

alter role mudengine_message_app set search_path to mudengine_message;
