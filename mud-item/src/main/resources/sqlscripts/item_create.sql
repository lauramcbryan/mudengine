set role mudengine_item;

create sequence mudengine_item.MUD_ITEM_SEQ;

CREATE TABLE MUDENGINE_ITEM.MUD_ITEM_CLASS (
		ITEM_CLASS     varchar(30) not null,
		SIZE			real not null,
		WEIGHT			real not null,
		DESCRIPTION		varchar(100),		
		CONSTRAINT MUD_ITEM_CLASS_PK PRIMARY KEY (ITEM_CLASS)
);

CREATE TABLE MUDENGINE_ITEM.MUD_ITEM_CLASS_ATTR (
		ITEM_CLASS     varchar(30) not null,
		ATTR_CODE		varchar(6) not null,
		ATTR_VALUE		integer not null default 0,
		CONSTRAINT MUD_ITEM_CLASS_ATTR_PK PRIMARY KEY (ITEM_CLASS, ATTR_CODE),
		FOREIGN KEY (ITEM_CLASS) REFERENCES MUDENGINE_ITEM.MUD_ITEM_CLASS(ITEM_CLASS) on delete cascade
);

CREATE TABLE MUDENGINE_ITEM.MUD_ITEM (
		ITEM_CODE		bigint not null,
		ITEM_CLASS     	varchar(30) not null,
		CURRENT_WORLD	varchar(30),
		CURRENT_PLACE	integer,
		CURRENT_OWNER   bigint,
		QUANTITY		integer not null default 1,
		CONSTRAINT MUD_ITEM_PK PRIMARY KEY (ITEM_CODE)
);

CREATE TABLE MUDENGINE_ITEM.MUD_ITEM_ATTR (
		ITEM_CODE		bigint not null,
		ATTR_CODE		varchar(6) not null,
		ATTR_VALUE		integer not null default 0,
		CONSTRAINT MUD_ITEM_ATTR_PK PRIMARY KEY (ITEM_CODE, ATTR_CODE),
		FOREIGN KEY (ITEM_CODE) REFERENCES MUDENGINE_ITEM.MUD_ITEM(ITEM_CODE) on delete cascade
);

reset role;
