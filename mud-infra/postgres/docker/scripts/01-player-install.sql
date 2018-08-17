create role mudengine_player_app login;
alter role mudengine_player_app with password 'mudengine_player_app';

create role mudengine_player CREATEDB;
create schema mudengine_player authorization mudengine_player;
grant mudengine_player to mudengine_player_app;

alter role mudengine_player_app set search_path to mudengine_player;
