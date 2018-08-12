set role MUDENGINE_BEING;

create sequence MUDENGINE_BEING.MUD_BEING_SEQ;

CREATE TABLE MUDENGINE_BEING.MUD_ATTRIBUTE (
		CODE			varchar(6) not null,
		NAME			varchar(30) not null,
		DESCRIPTION		varchar(100),		
		CONSTRAINT MUD_ATTRIBUTES_PK PRIMARY KEY (CODE)
);

CREATE TABLE MUDENGINE_BEING.MUD_SLOT (
		CODE			varchar(5) not null,
		NAME			varchar(30) not null,
		DESCRIPTION		varchar(100),		
		CONSTRAINT MUD_SLOT_PK PRIMARY KEY (CODE)
);


CREATE TABLE MUDENGINE_BEING.MUD_SKILL_CATEGORY (
		CODE			varchar(5) not null,
		NAME			varchar(30) not null,
		ATTR_CODE_BASED	varchar(5),
		DESCRIPTION		varchar(100),		
		CONSTRAINT MUD_SKILL_CATG_PK PRIMARY KEY (CODE),
		FOREIGN KEY (ATTR_CODE_BASED) REFERENCES MUDENGINE_BEING.MUD_ATTRIBUTE(CODE)
);
		
CREATE TABLE MUDENGINE_BEING.MUD_SKILL (
		CODE			varchar(20) not null,
		CATEGORY_CODE	varchar(5) not null,
		NAME			varchar(30) not null,
		DESCRIPTION		varchar(100),		
		CONSTRAINT MUD_SKILL_PK PRIMARY KEY (CODE),
		FOREIGN KEY (CATEGORY_CODE) REFERENCES MUDENGINE_BEING.MUD_SKILL_CATEGORY(CODE)
);

CREATE TABLE MUDENGINE_BEING.MUD_BEING_CLASS (
		CODE				varchar(20) not null,
		NAME				varchar(30) not null,
		DESCRIPTION     	varchar(200),
		SIZE				integer,
		WEIGHT_CAPACITY 	integer,
		CONSTRAINT MUD_BEING_CLASS_PK PRIMARY KEY (CODE)
);
		
CREATE TABLE MUDENGINE_BEING.MUD_BEING_CLASS_ATTR (
		CLASS_CODE		varchar(20) not null,
		CODE			varchar(5) not null,
		VALUE			integer not null default 0,
		CONSTRAINT MUD_BEING_CLASS_ATTR_PK PRIMARY KEY (CLASS_CODE, CODE),
		FOREIGN KEY (CLASS_CODE) REFERENCES MUDENGINE_BEING.MUD_BEING_CLASS(CODE) on delete cascade
);

CREATE TABLE MUDENGINE_BEING.MUD_BEING_CLASS_SKILL (
		CLASS_CODE		varchar(20) not null,
		CODE			varchar(20) not null,
		VALUE			integer not null default 0,
		CONSTRAINT MUD_BEING_CLASS_SKILLS_PK PRIMARY KEY (CLASS_CODE, CODE),
		FOREIGN KEY (CLASS_CODE) REFERENCES MUDENGINE_BEING.MUD_BEING_CLASS(CODE) on delete cascade
);

CREATE TABLE MUDENGINE_BEING.MUD_BEING_CLASS_SLOT (
		CLASS_CODE	varchar(20) not null,
		CODE		varchar(20) not null,
		CONSTRAINT MUD_BEING_CLASS_SLOT_PK PRIMARY KEY (CLASS_CODE, CODE),
		FOREIGN KEY (CLASS_CODE) REFERENCES MUDENGINE_BEING.MUD_BEING_CLASS(CODE) on delete cascade
);



CREATE TABLE MUDENGINE_BEING.MUD_BEING (
		CODE				bigint not null,
		BEING_CLASS_CODE	varchar(20) not null,
		BEING_TYPE			integer not null,
		NAME				varchar(50),
		PLAYER_ID			bigint,		
		CURRENT_WORLD		varchar(30) not null,
		CURRENT_PLACE		integer not null,
		QUANTITY			integer not null default 1,
		FOREIGN KEY (BEING_CLASS_CODE) REFERENCES MUDENGINE_BEING.MUD_BEING_CLASS(CODE),
		CONSTRAINT MUD_BEING_PK PRIMARY KEY (CODE)
);

CREATE TABLE MUDENGINE_BEING.MUD_BEING_ATTR (
		BEING_CODE	bigint not null,
		CODE		varchar(5) not null,
		VALUE		integer not null,
		CONSTRAINT MUD_BEING_ATTR_PK PRIMARY KEY (BEING_CODE, CODE),
		FOREIGN KEY (BEING_CODE) REFERENCES MUDENGINE_BEING.MUD_BEING(CODE) on delete cascade,
		FOREIGN KEY (CODE) REFERENCES MUDENGINE_BEING.MUD_ATTRIBUTE(CODE)
);

CREATE TABLE MUDENGINE_BEING.MUD_BEING_SKILL (
		BEING_CODE	bigint not null,
		CODE		varchar(20) not null,
		VALUE 		integer not null,
		CONSTRAINT MUD_BEING_SKILLS_PK PRIMARY KEY (BEING_CODE, CODE),
		FOREIGN KEY (BEING_CODE) REFERENCES MUDENGINE_BEING.MUD_BEING(CODE) on delete cascade,
		FOREIGN KEY (CODE) REFERENCES MUDENGINE_BEING.MUD_SKILL(CODE)
);

CREATE TABLE MUDENGINE_BEING.MUD_BEING_SLOT (
		BEING_CODE	bigint not null,
		CODE		varchar(20) not null,
		ITEM_CODE	bigint,
		CONSTRAINT MUD_BEING_SLOT_PK PRIMARY KEY (BEING_CODE, CODE),
		FOREIGN KEY (BEING_CODE) REFERENCES MUDENGINE_BEING.MUD_BEING(CODE) on delete cascade,
		FOREIGN KEY (CODE) REFERENCES MUDENGINE_BEING.MUD_SLOT(CODE)
);


CREATE TABLE MUDENGINE_BEING.MUD_BEING_ATTR_MODIFIER (
		BEING_CODE		bigint not null,
		CODE			varchar(5) not null,
		ORIGIN_CODE		varchar(20) not null,
		ORIGIN_TYPE		varchar(30) not null,
		ATTR_OFFSET			real not null,
		END_TURN		integer,
		CONSTRAINT MUD_BEING_ATTR_MOD_PK PRIMARY KEY (BEING_CODE, CODE, ORIGIN_CODE, ORIGIN_TYPE),
		FOREIGN KEY (BEING_CODE) REFERENCES MUDENGINE_BEING.MUD_BEING(CODE)  on delete cascade,
		FOREIGN KEY (CODE) REFERENCES MUDENGINE_BEING.MUD_ATTRIBUTE(CODE)
);

CREATE TABLE MUDENGINE_BEING.MUD_BEING_SKILL_MODIFIER (
		BEING_CODE		bigint not null,
		CODE			varchar(20) not null,
		ORIGIN_CODE		varchar(20) not null,
		ORIGIN_TYPE		varchar(30) not null,
		SKILL_OFFSET 			real not null,
		END_TURN		integer,
		CONSTRAINT MUD_BEING_SKILL_MOD_PK PRIMARY KEY (BEING_CODE, CODE, ORIGIN_CODE, ORIGIN_TYPE),
		FOREIGN KEY (BEING_CODE) REFERENCES MUDENGINE_BEING.MUD_BEING(CODE)  on delete cascade,
		FOREIGN KEY (CODE) REFERENCES MUDENGINE_BEING.MUD_SKILL(CODE)
);

reset role;