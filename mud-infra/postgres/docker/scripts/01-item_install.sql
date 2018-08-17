create role mudengine_item_app login;
alter role mudengine_item_app with password 'mudengine_item_app';

create role mudengine_item CREATEDB;
create schema mudengine_item authorization mudengine_item;
grant mudengine_item to mudengine_item_app;

alter role mudengine_item_app set search_path to mudengine_item;
