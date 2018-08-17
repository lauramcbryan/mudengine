set role mudengine_item;

create sequence mudengine_item.MUD_ITEM_SEQ;

CREATE TABLE MUDENGINE_ITEM.MUD_ITEM_CLASS (
		CODE     			varchar(30) not null,
		NAME				varchar(30) not null,
		SIZE				real not null,
		WEIGHT				real not null,
		DESCRIPTION			varchar(100),		
		DEMISED_CLASS_CODE 	varchar(30),
		CONSTRAINT MUD_ITEM_CLASS_PK PRIMARY KEY (CODE)
);

CREATE TABLE MUDENGINE_ITEM.MUD_ITEM_CLASS_ATTR (
		CLASS_CODE      varchar(30) not null,
		CODE			varchar(6) not null,
		VALUE			integer not null default 0,
		CONSTRAINT MUD_ITEM_CLASS_ATTR_PK PRIMARY KEY (CLASS_CODE, CODE),
		FOREIGN KEY (CLASS_CODE) REFERENCES MUDENGINE_ITEM.MUD_ITEM_CLASS(CODE) on delete cascade
);

CREATE TABLE MUDENGINE_ITEM.MUD_ITEM (
		CODE			bigint not null,
		NAME			varchar(30),
		CLASS_CODE     	varchar(30) not null,
		CURRENT_WORLD	varchar(30),
		CURRENT_PLACE	integer,
		CURRENT_OWNER   bigint,
		QUANTITY		integer not null default 1,
		CONSTRAINT MUD_ITEM_PK PRIMARY KEY (CODE)
);

CREATE TABLE MUDENGINE_ITEM.MUD_ITEM_ATTR (
		ITEM_CODE	bigint not null,
		CODE		varchar(6) not null,
		VALUE		integer not null default 0,
		CONSTRAINT MUD_ITEM_ATTR_PK PRIMARY KEY (ITEM_CODE, CODE),
		FOREIGN KEY (ITEM_CODE) REFERENCES MUDENGINE_ITEM.MUD_ITEM(CODE) on delete cascade
);

reset role;
