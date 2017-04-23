-- attributes
INSERT INTO mudengine_being.mud_attribute(attr_code, name) VALUES ('STR', 'Strength');
INSERT INTO mudengine_being.mud_attribute(attr_code, name) VALUES ('DEX', 'Dexterity');
INSERT INTO mudengine_being.mud_attribute(attr_code, name) VALUES ('INT', 'Intelligence');
INSERT INTO mudengine_being.mud_attribute(attr_code, name) VALUES ('CHR', 'Charisma');
INSERT INTO mudengine_being.mud_attribute(attr_code, name) VALUES ('HP', 'HitPoints');
INSERT INTO mudengine_being.mud_attribute(attr_code, name) VALUES ('CGOCAP', 'Cargo capacity');
INSERT INTO mudengine_being.mud_attribute(attr_code, name) VALUES ('SIZCAP', 'Size capacity');
INSERT INTO mudengine_being.mud_attribute(attr_code, name) VALUES ('CARRIED', 'Cargo carried');


-- skill categories
INSERT INTO mudengine_being.mud_skill_category(category_code, name, attr_code_based) VALUES ('ADM', 'Administrator', 'CHR');
INSERT INTO mudengine_being.mud_skill_category(category_code, name, attr_code_based) VALUES ('ENG', 'Engineering', 'INT');
INSERT INTO mudengine_being.mud_skill_category(category_code, name, attr_code_based) VALUES ('FIGHT', 'Brawler', 'STR');
INSERT INTO mudengine_being.mud_skill_category(category_code, name, attr_code_based) VALUES ('SHOOT', 'Shooter', 'DEX');
INSERT INTO mudengine_being.mud_skill_category(category_code, name, attr_code_based) VALUES ('MIND', 'Scholar', 'INT');

-- skills
INSERT INTO mudengine_being.mud_skill(skill_code, category_code, name, description) VALUES ('FARMER', 'ADM', 'Farmer', 'Tender the fields');
INSERT INTO mudengine_being.mud_skill(skill_code, category_code, name, description) VALUES ('BRAWLER', 'FIGHT', 'Fighter', 'Fight with bare hands');
INSERT INTO mudengine_being.mud_skill(skill_code, category_code, name, description) VALUES ('SHOOTER', 'SHOOT', 'Shooter', 'Shoot with gun');
INSERT INTO mudengine_being.mud_skill(skill_code, category_code, name, description) VALUES ('BUILDER', 'ENG', 'Builder', 'Constructs buildings');
INSERT INTO mudengine_being.mud_skill(skill_code, category_code, name, description) VALUES ('SCHOLAR', 'MIND', 'Scholar', 'Studies');

INSERT INTO mudengine_being.mud_player(player_id, login, password, name) VALUES (1, 'silverheart', 'password', 'James McBryan');

INSERT INTO mudengine_being.mud_being(being_code, being_class, name, player_id, current_place, current_world) VALUES (1, 'HUMAN', 'Slash Calliber', 1, 1, 'aforgotten');

INSERT INTO mudengine_being.mud_being_attr(being_code, attr_code, attr_value) VALUES (1, 'STR', 15);
INSERT INTO mudengine_being.mud_being_attr(being_code, attr_code, attr_value) VALUES (1, 'DEX', 6);
INSERT INTO mudengine_being.mud_being_attr(being_code, attr_code, attr_value) VALUES (1, 'INT', 6);
INSERT INTO mudengine_being.mud_being_attr(being_code, attr_code, attr_value) VALUES (1, 'CHR', 4);
INSERT INTO mudengine_being.mud_being_attr(being_code, attr_code, attr_value) VALUES (1, 'HP', 20);

