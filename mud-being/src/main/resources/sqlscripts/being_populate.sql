-- attributes
INSERT INTO mudengine_being.mud_attribute(code, name) VALUES ('STR', 'Strength');
INSERT INTO mudengine_being.mud_attribute(code, name) VALUES ('DEX', 'Dexterity');
INSERT INTO mudengine_being.mud_attribute(code, name) VALUES ('INT', 'Intelligence');
INSERT INTO mudengine_being.mud_attribute(code, name) VALUES ('CHR', 'Charisma');
INSERT INTO mudengine_being.mud_attribute(code, name) VALUES ('HP', 'HitPoints');
INSERT INTO mudengine_being.mud_attribute(code, name) VALUES ('CGOCAP', 'Cargo capacity');
INSERT INTO mudengine_being.mud_attribute(code, name) VALUES ('SIZCAP', 'Size capacity');
INSERT INTO mudengine_being.mud_attribute(code, name) VALUES ('CGO', 'Cargo carried');


-- skill categories
INSERT INTO mudengine_being.mud_skill_category(code, name, attr_code_based) VALUES ('ADM', 'Administrator', 'CHR');
INSERT INTO mudengine_being.mud_skill_category(code, name, attr_code_based) VALUES ('ENG', 'Engineering', 'INT');
INSERT INTO mudengine_being.mud_skill_category(code, name, attr_code_based) VALUES ('FIGHT', 'Brawler', 'STR');
INSERT INTO mudengine_being.mud_skill_category(code, name, attr_code_based) VALUES ('SHOOT', 'Shooter', 'DEX');
INSERT INTO mudengine_being.mud_skill_category(code, name, attr_code_based) VALUES ('MIND', 'Scholar', 'INT');

-- skills
INSERT INTO mudengine_being.mud_skill(code, category_code, name, description) VALUES ('FARMER', 'ADM', 'Farmer', 'Tender the fields');
INSERT INTO mudengine_being.mud_skill(code, category_code, name, description) VALUES ('BRAWLER', 'FIGHT', 'Fighter', 'Fight with bare hands');
INSERT INTO mudengine_being.mud_skill(code, category_code, name, description) VALUES ('SHOOTER', 'SHOOT', 'Shooter', 'Shoot with gun');
INSERT INTO mudengine_being.mud_skill(code, category_code, name, description) VALUES ('BUILDER', 'ENG', 'Builder', 'Constructs buildings');
INSERT INTO mudengine_being.mud_skill(code, category_code, name, description) VALUES ('SCHOLAR', 'MIND', 'Scholar', 'Studies');


-- being classes

-- Human
INSERT INTO mudengine_being.mud_being_class(code, name, description, size, weight_capacity) VALUES ('HUMAN', 'Human', 'Average Human being', 1, 10);
INSERT INTO mudengine_being.mud_being_class_attr(class_code, code, value) VALUES ('HUMAN', 'STR', 8);
INSERT INTO mudengine_being.mud_being_class_attr(class_code, code, value) VALUES ('HUMAN', 'DEX', 8);
INSERT INTO mudengine_being.mud_being_class_attr(class_code, code, value) VALUES ('HUMAN', 'INT', 8);
INSERT INTO mudengine_being.mud_being_class_attr(class_code, code, value) VALUES ('HUMAN', 'CHR', 8);
INSERT INTO mudengine_being.mud_being_class_attr(class_code, code, value) VALUES ('HUMAN', 'HP', 10);
INSERT INTO mudengine_being.mud_being_class_skill(class_code, code, value) VALUES ('HUMAN', 'FARMER', 10);
INSERT INTO mudengine_being.mud_being_class_skill(class_code, code, value) VALUES ('HUMAN', 'BRAWLER', 50);


-- Crinos
INSERT INTO mudengine_being.mud_being_class(code, name, description, size, weight_capacity) VALUES ('CRINOS', 'Werewolf', 'Werewolf in Crinos form', 2, 25);
INSERT INTO mudengine_being.mud_being_class_attr(class_code, code, value) VALUES ('CRINOS', 'STR', 12);
INSERT INTO mudengine_being.mud_being_class_attr(class_code, code, value) VALUES ('CRINOS', 'DEX', 10);
INSERT INTO mudengine_being.mud_being_class_attr(class_code, code, value) VALUES ('CRINOS', 'INT', 6);
INSERT INTO mudengine_being.mud_being_class_attr(class_code, code, value) VALUES ('CRINOS', 'CHR', 6);
INSERT INTO mudengine_being.mud_being_class_attr(class_code, code, value) VALUES ('CRINOS', 'HP', 15);
INSERT INTO mudengine_being.mud_being_class_skill(class_code, code, value) VALUES ('CRINOS', 'BRAWLER', 80);

-- Ox
INSERT INTO mudengine_being.mud_being_class(code, name, size, weight_capacity) VALUES ('OX', 'Ox', 4, 50);
INSERT INTO mudengine_being.mud_being_class_attr(class_code, code, value) VALUES ('OX', 'STR', 15);
INSERT INTO mudengine_being.mud_being_class_attr(class_code, code, value) VALUES ('OX', 'DEX', 6);
INSERT INTO mudengine_being.mud_being_class_attr(class_code, code, value) VALUES ('OX', 'INT', 6);
INSERT INTO mudengine_being.mud_being_class_attr(class_code, code, value) VALUES ('OX', 'CHR', 4);
INSERT INTO mudengine_being.mud_being_class_attr(class_code, code, value) VALUES ('OX', 'HP', 20);


INSERT INTO mudengine_being.mud_being(code, being_class_code, name, player_id, current_place, current_world, being_type) VALUES (1, 'HUMAN', 'Slash Calliber', 1, 1, 'aforgotten', 3);

INSERT INTO mudengine_being.mud_being_attr(being_code, code, value) VALUES (1, 'STR', 15);
INSERT INTO mudengine_being.mud_being_attr(being_code, code, value) VALUES (1, 'DEX', 6);
INSERT INTO mudengine_being.mud_being_attr(being_code, code, value) VALUES (1, 'INT', 6);
INSERT INTO mudengine_being.mud_being_attr(being_code, code, value) VALUES (1, 'CHR', 4);
INSERT INTO mudengine_being.mud_being_attr(being_code, code, value) VALUES (1, 'HP', 20);

