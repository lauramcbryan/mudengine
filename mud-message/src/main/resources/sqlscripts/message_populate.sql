delete from mud_message_locale;
delete from mud_message_parm;
delete from mud_message;

delete from mud_message_locale;

insert into mud_message_locale(message_key, locale, message_text) values('SIMPLESTR', 'en_US', '%s');
insert into mud_message_locale(message_key, locale, message_text) values('NOTHING', 'en_US', 'Nothing' );
insert into mud_message_locale(message_key, locale, message_text) values('NOBODY', 'en_US', 'Nobody' );

insert into mud_message_locale(message_key, locale, message_text) values('SIMPLEBEING', 'en_US', 'A %s called %s');


insert into mud_message_locale(message_key, locale, message_text) values('ATTRHEADER', 'en_US', 'Attributes' );
insert into mud_message_locale(message_key, locale, message_text) values('ATTR', 'en_US', '%s %s' );
insert into mud_message_locale(message_key, locale, message_text) values('ATTRMOD', 'en_US', '%s %s (%s)' );

insert into mud_message_locale(message_key, locale, message_text) values('SKILLHEADER', 'en_US', 'Skills' );
insert into mud_message_locale(message_key, locale, message_text) values('SKILL', 'en_US', '%s %s' );
insert into mud_message_locale(message_key, locale, message_text) values('SKILLMOD', 'en_US', '%s %s (%s)' );


-- WALK
insert into mud_message_locale(message_key, locale, message_text) values('NOEXIT', 'en_US', 'There''s no exit in that direction.');
insert into mud_message_locale(message_key, locale, message_text) values('NOEXIT', 'en_US', 'This exit is closed.');
insert into mud_message_locale(message_key, locale, message_text) values('EXITCLOSED', 'en_US', 'This passage is closed.');

-- TAKE
insert into mud_message_locale(message_key, locale, message_text) values('NOPLACE', 'en_US', 'You aren''t in %s' );
insert into mud_message_locale(message_key, locale, message_text) values('NOBEING', 'en_US', '%s isn''t here');
insert into mud_message_locale(message_key, locale, message_text) values('NOTITEM', 'en_US', 'Theresn''t a %s here');
insert into mud_message_locale(message_key, locale, message_text) values('YOUTAKE', 'en_US', 'You take %s');

-- DROP
insert into mud_message_locale(message_key, locale, message_text) values('NOTHAVE', 'en_US', 'You don´t have %s');
insert into mud_message_locale(message_key, locale, message_text) values('YOUDROP', 'en_US', 'You drop %s');



insert into mud_message_locale(message_key, locale, message_text) values('THISPLACEIS', 'en_US', 'You are in a %s' );
insert into mud_message_locale(message_key, locale, message_text) values('THISPLACEDESC', 'en_US', 'This place is a %s');
insert into mud_message_locale(message_key, locale, message_text) values('EXITHEADER', 'en_US', 'Exits from here:' );
insert into mud_message_locale(message_key, locale, message_text) values('EXIT', 'en_US', 'At [%s] there''s a %s' );
insert into mud_message_locale(message_key, locale, message_text) values('NOEXIT', 'en_US', 'There''s no exits from here' );

insert into mud_message_locale(message_key, locale, message_text) values('BEINGHEADER', 'en_US', 'Beings at this place:' );
insert into mud_message_locale(message_key, locale, message_text) values('PACKOFBEINGS', 'en_US', 'A pack of %s');
insert into mud_message_locale(message_key, locale, message_text) values('GROUPOFBEINGS', 'en_US', 'A group of %s');

insert into mud_message_locale(message_key, locale, message_text) values('ITEMHEADER', 'en_US', 'Items at this place:' );



insert into mud_message_locale(message_key, locale, message_text) values('YOUARE', 'en_US', 'You are %s, proud member of %s.');
insert into mud_message_locale(message_key, locale, message_text) values('YOUAREDESC', 'en_US', 'Your classe are %s');

insert into mud_message_locale(message_key, locale, message_text) values('YOUHAVEHEADER', 'en_US', 'You have:' );

