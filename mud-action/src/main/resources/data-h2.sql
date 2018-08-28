insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale, run_type) 
	values(1, 'WALK', 'WALK', 'Move to another place', 'WALK <DIRECTION>', 'en-US', 'SIMPLE');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required) 
	values (1, 'targetCode', 'Please input the direction', 'DIRECTION', 1);

insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale, run_type) 
	values(101, 'WALK', 'VÁ PARA', 'Ir para outro lugar', 'VÁ PARA <DIREÇÃO>', 'pt-BR', 'SIMPLE');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required) 
	values (101, 'targetCode', 'Por favor indique a direção', 'DIRECTION', 1);


-- ========================================================================================================

insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale, run_type) 
	values(5, 'TAKE', 'TAKE', 'Take an item', 'TAKE <ITEM>', 'en-US', 'SIMPLE');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required, domain_values, default_value) 
	values (5, 'targetCode', 'Please input the item', 'ITEM', 1, null, null);

insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale, run_type) 
	values(105, 'TAKE', 'PEGUE', 'Pegar um item', 'PEGUE <ITEM>', 'pt-BR', 'SIMPLE');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required, domain_values, default_value) 
	values (105, 'targetCode', 'Por favor, indique o item', 'ITEM', 1, null, null);


-- ========================================================================================================

insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale, run_type) 
	values(6, 'DROP', 'DROP', 'Drop an item', 'DROP <ITEM>', 'en-US', 'SIMPLE');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required, domain_values, default_value) 
	values (6, 'targetCode', 'Please input the item', 'ITEM', 1, null, null);


insert into mud_action_class_cmd(command_id, action_class_code, verb, description, usage, locale, run_type) 
	values(106, 'DROP', 'SOLTE', 'Soltar um item', 'PEGUE <ITEM>', 'pt-BR', 'SIMPLE');

insert into mud_action_class_cmd_parameter(command_id, name, input_message, type, required, domain_values, default_value) 
	values (106, 'targetCode', 'Por favor, indique o item', 'ITEM', 1, null, null);


-- ========================================================================================================
