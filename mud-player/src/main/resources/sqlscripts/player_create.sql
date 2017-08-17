set role MUDENGINE_PLAYER;

create sequence MUDENGINE_PLAYER.MUD_SESSION_SEQ;
create sequence MUDENGINE_PLAYER.MUD_PLAYER_SEQ;


CREATE TABLE MUDENGINE_PLAYER.MUD_PLAYER (
		PLAYER_ID		bigint not null,
		USERNAME		varchar(30) not null,
		PASSWORD		varchar(64) not null,
		EMAIL			varchar(30) not null,
		NAME			varchar(30),
		LANGUAGE		varchar(30),
		COUNTRY			varchar(5),
		STATUS			integer not null default 0,
		CONSTRAINT MUD_PLAYER_PK PRIMARY KEY (PLAYER_ID)
);	

CREATE TABLE MUDENGINE_PLAYER.MUD_PLAYER_SESSION (
	SESSION_ID			bigint not null,
	PLAYER_ID			bigint not null,
	SESSION_START		timestamp not null default current_timestamp,
	SESSION_END			timestamp,
	NOTIFICATION_ABLE	boolean,
	CONSTRAINT MUD_PLAYER_SESSION_PK PRIMARY KEY (SESSION_ID),
	FOREIGN KEY (PLAYER_ID) REFERENCES MUDENGINE_PLAYER.MUD_PLAYER(PLAYER_ID) on delete cascade
);
