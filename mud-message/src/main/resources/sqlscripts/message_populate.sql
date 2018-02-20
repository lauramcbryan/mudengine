delete from mud_message_locale;
delete from mud_message_parm;
delete from mud_message;

delete from mud_message_locale;

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

