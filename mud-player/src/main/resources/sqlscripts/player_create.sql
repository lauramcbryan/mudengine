set role MUDENGINE_PLAYER;

create sequence MUDENGINE_PLAYER.MUD_SESSION_SEQ;
create sequence MUDENGINE_PLAYER.MUD_PLAYER_SEQ;


CREATE TABLE MUDENGINE_PLAYER.MUD_PLAYER (
		PLAYER_ID		bigint not null,
		USERNAME		varchar(30) not null UNIQUE,
		PASSWORD		varchar(64) not null,
		EMAIL			varchar(30) not null,
		NAME			varchar(30),
		LOCALE  		varchar(30),
		CREATE_DATE		timestamp not null default current_timestamp,		
		STATUS			integer not null default 0,
		CONSTRAINT MUD_PLAYER_PK PRIMARY KEY (PLAYER_ID)
);	

CREATE TABLE MUDENGINE_PLAYER.MUD_PLAYER_BEING (
	PLAYER_ID			bigint not null,
	BEING_CODE			bigint not null,	
	BEING_NAME			varchar(50) not null,	
	BEING_CLASS			varchar(30) not null,
	LAST_PLAYED			timestamp,	
	CONSTRAINT MUD_PLAYER_BEING_PK PRIMARY KEY (PLAYER_ID, BEING_CODE),
	FOREIGN KEY (PLAYER_ID) REFERENCES MUDENGINE_PLAYER.MUD_PLAYER(PLAYER_ID) on delete cascade
);


CREATE TABLE MUDENGINE_PLAYER.MUD_PLAYER_SESSION (
	SESSION_ID			bigint not null,
	PLAYER_ID			bigint not null,
	SESSION_START		timestamp not null default current_timestamp,
	SESSION_END			timestamp,
	CLIENT_TYPE			varchar(10),
	IP_ADDRESS			varchar(16),
	BEING_CODE			bigint,
	
	CONSTRAINT MUD_PLAYER_SESSION_PK PRIMARY KEY (SESSION_ID),
	FOREIGN KEY (PLAYER_ID) REFERENCES MUDENGINE_PLAYER.MUD_PLAYER(PLAYER_ID) on delete cascade
);
