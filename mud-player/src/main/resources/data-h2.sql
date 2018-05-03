delete from mud_player;

alter sequence MUD_PLAYER_SEQ restart with 4;
alter sequence MUD_SESSION_SEQ restart with 2;

insert into mud_player(player_id, username, password, email, status) values (1, 'testuser', 'pass', 'josieloliveira@hotmail.com', 1);
insert into mud_player(player_id, username, password, email, status) values (2, 'pendinguser', 'pass', 'josieloliveira@hotmail.com', 0);

insert into mud_player(player_id, username, password, email, status) values (3, 'sessionuser', 'pass', 'josieloliveira@hotmail.com', 1);

insert into mud_player_session(session_id, player_id) values (1, 3);
	