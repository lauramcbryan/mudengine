create role mudengine_item_class_app login;
alter role mudengine_item_class_app with password 'mudengine_item_class_app';

create role mudengine_item_class CREATEDB;
create schema mudengine_item_class authorization mudengine_item_class;
grant mudengine_item_class to mudengine_item_class_app;

alter role mudengine_item_class_app set search_path to mudengine_item_class;
