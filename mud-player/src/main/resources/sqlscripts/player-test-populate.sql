delete from mud_player;
select nextval('MUD_PLAYER_SEQ');
insert into mud_player(player_id, username, password, email, status) values (1, 'josiel', 'pass', 'josieloliveira@hotmail.com', 1);