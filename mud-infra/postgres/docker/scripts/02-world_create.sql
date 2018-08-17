set role mudengine_world;

create sequence mudengine_world.MUD_PLACE_SEQ;

CREATE TABLE mudengine_world.MUD_PLACE_CLASS (
	CODE				varchar(20) NOT NULL,
	NAME				varchar(30) NOT NULL,
	DESCRIPTION			varchar(500),
	SIZE_CAPACITY		integer,
	WEIGHT_CAPACITY		integer,
	PARENT_CLASS_CODE   varchar(20),
	DEMISED_CLASS_CODE  varchar(20),
	BUILD_COST			integer,
	BUILD_EFFORT		integer,	
	CONSTRAINT MUD_PLACE_CLASS_PK PRIMARY KEY (CODE)
);


CREATE TABLE mudengine_world.MUD_PLACE_CLASS_ATTR (
	CLASS_CODE	varchar(20) NOT NULL,
	CODE		varchar(5) not null,
	VALUE		integer not null default 0,
	CONSTRAINT MUD_PLACE_CLASS_ATTR_PK PRIMARY KEY (CLASS_CODE, CODE),
	FOREIGN KEY (CLASS_CODE) REFERENCES mudengine_world.MUD_PLACE_CLASS(CODE) on delete cascade
);

CREATE TABLE mudengine_world.MUD_PLACE  (
	CODE		integer NOT NULL,
	CLASS_CODE	varchar(20) NOT NULL,
	NAME		varchar(30),
	CONSTRAINT MUD_PLACE_PK PRIMARY KEY (CODE),
	FOREIGN KEY (CLASS_CODE) REFERENCES mudengine_world.MUD_PLACE_CLASS(CODE)
);
	
	
CREATE TABLE mudengine_world.MUD_PLACE_EXIT (
	PLACE_CODE			integer NOT NULL,
	NAME				varchar(30) NOT NULL,
	DIRECTION			varchar(10) NOT NULL,
	OPENED				boolean NOT NULL DEFAULT 'true',
	LOCKED				boolean NOT NULL DEFAULT 'false',
	LOCKABLE			boolean NOT NULL DEFAULT 'false',
	VISIBLE				boolean NOT NULL DEFAULT 'true',
	TARGET_PLACE_CODE	integer NOT NULL,
	CONSTRAINT MUD_PLACE_EXITS_PK PRIMARY KEY (PLACE_CODE, DIRECTION),
	FOREIGN KEY (PLACE_CODE) REFERENCES mudengine_world.MUD_PLACE(CODE) on delete cascade,
	FOREIGN KEY (TARGET_PLACE_CODE) REFERENCES mudengine_world.MUD_PLACE(CODE) on delete cascade
);

CREATE TABLE mudengine_world.MUD_PLACE_ATTR (
	PLACE_CODE	integer NOT NULL,
	CODE		varchar(5) not null,
	VALUE		integer not null default 0,
	CONSTRAINT MUD_PLACE_ATTR_PK PRIMARY KEY (PLACE_CODE, CODE),
	FOREIGN KEY (PLACE_CODE) REFERENCES mudengine_world.MUD_PLACE(CODE) on delete cascade
);


reset role;