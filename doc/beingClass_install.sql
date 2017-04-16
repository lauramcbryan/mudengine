create role mudengine_being_class_app login;
alter role mudengine_being_class_app with password 'mudengine_being_class_app';

create role mudengine_being_class CREATEDB;
create schema mudengine_being_class authorization mudengine_being_class;
grant mudengine_being_class to mudengine_being_class_app;

alter role mudengine_being_class_app set search_path to mudengine_being_class;
