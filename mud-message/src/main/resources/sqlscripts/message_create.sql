set role mudengine_message;

create sequence mudengine_message.MUD_MESSAGE_SEQ;

CREATE TABLE mudengine_message.MUD_MESSAGE (
	MESSAGE_ID			bigint NOT NULL,
	BEING_CODE			bigint NOT NULL,
	SENDER_CODE			bigint,
	SENDER_NAME			varchar(50),
	INSERT_DATE			timestamp not null default current_timestamp,
	MESSAGE_KEY			varchar(1024) not null,
	READ_FLAG			boolean,
	CONSTRAINT MUD_MESSAGE_PK PRIMARY KEY (MESSAGE_ID)
);


CREATE TABLE mudengine_message.MUD_MESSAGE_PARM (
	MESSAGE_ID			bigint NOT NULL,
	EVAL_ORDER			integer NOT NULL,
	VALUE 				varchar(100) NOT NULL,
	CONSTRAINT MUD_MESSAGE_PARM_PK PRIMARY KEY (MESSAGE_ID, EVAL_ORDER),
	FOREIGN KEY (MESSAGE_ID) REFERENCES mudengine_message.MUD_MESSAGE(MESSAGE_ID) on delete cascade
);

CREATE TABLE mudengine_message.MUD_MESSAGE_LOCALE (
	MESSAGE_KEY			varchar(50) not null,
	LOCALE 				varchar(50) not null,
	MESSAGE_TEXT 		varchar(1024) not null
	CONSTRAINT MUD_MESSAGE_LOCALE_PK PRIMARY KEY (MESSAGE_KEY, LOCALE)
);
