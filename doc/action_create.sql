set role MUDENGINE_ACTION;

create sequence MUDENGINE_ACTION.MUD_ACTION_SEQ;

CREATE TABLE MUDENGINE_ACTION.MUD_ACTION_CLASS (
		ACTION_CLASS_CODE	varchar(15) not null,
		VERB				varchar(10) not null,
		CONSTRAINT MUD_ACTION_CLASS_PK PRIMARY KEY (ACTION_CLASS_CODE)
);

CREATE TABLE MUDENGINE_ACTION.MUD_ACTION_CLASS_PREREQ (
		ACTION_CLASS_CODE	varchar(15) not null,
		EVAL_ORDER			integer not null,
		EXPRESSION			varchar(200) not null,
		MESSAGE_CODE		integer,
		CONSTRAINT MUD_ACTION_CLASS_PREREQ_PK PRIMARY KEY (ACTION_CLASS_CODE, EVAL_ORDER),
		FOREIGN KEY (ACTION_CLASS_CODE) REFERENCES MUDENGINE_ACTION.MUD_ACTION_CLASS(ACTION_CLASS_CODE)
);

CREATE TABLE MUDENGINE_ACTION.MUD_ACTION_CLASS_COST (
		ACTION_CLASS_CODE	varchar(15) not null,
		EVAL_ORDER			integer not null,
		EXPRESSION			varchar(200) not null,
		MESSAGE_CODE		integer,
		CONSTRAINT MUD_ACTION_CLASS_COST_PK PRIMARY KEY (ACTION_CLASS_CODE, EVAL_ORDER),
		FOREIGN KEY (ACTION_CLASS_CODE) REFERENCES MUDENGINE_ACTION.MUD_ACTION_CLASS(ACTION_CLASS_CODE)
);

CREATE TABLE MUDENGINE_ACTION.MUD_ACTION_CLASS_EFFECT (
		ACTION_CLASS_CODE	varchar(15) not null,
		EVAL_ORDER			integer not null,
		EXPRESSION			varchar(200) not null,
		MESSAGE_CODE		integer,
		CONSTRAINT MUD_ACTION_CLASS_EFFECT_PK PRIMARY KEY (ACTION_CLASS_CODE, EVAL_ORDER),
		FOREIGN KEY (ACTION_CLASS_CODE) REFERENCES MUDENGINE_ACTION.MUD_ACTION_CLASS(ACTION_CLASS_CODE)
);
		
CREATE TABLE MUDENGINE_ACTION.MUD_ACTION (
		ACTION_UID			bigint not null,
		ISSUER_CODE			bigint not null,
		ACTOR_CODE			bigint not null,		
		ACTION_CLASS_CODE	varchar(15) not null,
		MEDIATOR_CODE		bigint,
		WORLD_NAME			varchar(30) not null,
		PLACE_CODE			integer not null,
		TARGET_CODE			varchar(20) not null,
		TARGET_TYPE			varchar(20) not null,
		
		START_TURN			bigint,
		END_TURN			bigint,
		CUR_STATE			integer not null default 0,  -- 0=NotStarted, 1=Started, 2=Completed, 3=Cancelled, 4=Refused
		SUCCESS_RATE		real,
		
		CONSTRAINT MUD_ACTION_PK PRIMARY KEY (ACTION_UID),
		FOREIGN KEY (ACTION_CLASS_CODE) REFERENCES MUDENGINE_ACTION.MUD_ACTION_CLASS(ACTION_CLASS_CODE)
);
