delete from mud_player;

select nextval('MUD_PLAYER_SEQ');

insert into mud_player(player_id, username, password, email, status) values (1, 'testuser', 'pass', 'josieloliveira@hotmail.com', 1);
insert into mud_player(player_id, username, password, email, status) values (2, 'pendinguser', 'pass', 'josieloliveira@hotmail.com', 0);

insert into mud_player(player_id, username, password, email, status) values (3, 'sessionuser', 'pass', 'josieloliveira@hotmail.com', 1);

insert into mud_player_session(session_id, player_id) values (1, 3);
	