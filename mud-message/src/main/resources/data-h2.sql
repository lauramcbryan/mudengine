insert into mud_message_locale(message_key, locale, message_text) values('MESSAGE1', 'en_US', 'Message in en_US');
insert into mud_message_locale(message_key, locale, message_text) values('MESSAGE1', 'pt_BR', 'Mensagem em pt_BR');
insert into mud_message_locale(message_key, locale, message_text) values('MESSAGE2', 'en_US', 'Message with %s parameter');
insert into mud_message_locale(message_key, locale, message_text) values('MESSAGE2', 'pt_BR', 'Mensagem com um parâmetro %s');
insert into mud_message_locale(message_key, locale, message_text) values('MESSAGE3', 'en_US', 'Message with two(%s and %d) parameters');
insert into mud_message_locale(message_key, locale, message_text) values('MESSAGE3', 'pt_BR', 'Mensagem com dois parâmetros (%s e %d).');

insert into mud_message_locale(message_key, locale, message_text) values('VALUE1', 'en_US', '  First');
insert into mud_message_locale(message_key, locale, message_text) values('VALUE1', 'pt_BR', '  Primeiro');


-- plain message
insert into mud_message(message_id, being_code, insert_turn, message_key, read_flag) values(1, 1, 10, 'Test message', false);

-- localized message
insert into mud_message(message_id, being_code, insert_turn, message_key, read_flag) values(2, 1, 11, '{str:MESSAGE1}', false);

-- placeholder message
insert into mud_message(message_id, being_code, insert_turn, message_key, read_flag) values(3, 1, 12, '{str:MESSAGE2}', false);
insert into mud_message(message_id, being_code, insert_turn, message_key, read_flag) values(4, 1, 13, '{str:MESSAGE3}', false);
insert into mud_message_parm(message_id, eval_order, message_text) values(3, 1, 'ONE');
insert into mud_message_parm(message_id, eval_order, message_text) values(4, 1, 'TEXT');
insert into mud_message_parm(message_id, eval_order, message_text) values(4, 2, 'NUMERIC');

-- localized parameter
insert into mud_message(message_id, being_code, insert_turn, message_key, read_flag) values(5, 1, 14, '{str:MESSAGE1}', false);
insert into mud_message_parm(message_id, eval_order, message_text) values(5, 1, '{str:VALUE1}');

-- message from another being
insert into mud_message(message_id, being_code, insert_turn, message_key, read_flag) values(6, 2, 15, 'Another being message', false);

