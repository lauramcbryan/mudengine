-- being classes

-- Human
INSERT INTO mud_being_class(being_class, name, description, size, weight_capacity) VALUES ('HUMAN', 'Human', 'Average Human being', 1, 10);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('HUMAN', 'STR', 8);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('HUMAN', 'DEX', 8);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('HUMAN', 'INT', 8);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('HUMAN', 'CHR', 8);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('HUMAN', 'HP', 10);
INSERT INTO mud_being_class_skill(being_class, skill_code, skill_value) VALUES ('HUMAN', 'FARMER', 10);
INSERT INTO mud_being_class_skill(being_class, skill_code, skill_value) VALUES ('HUMAN', 'BRAWLER', 50);


-- Crinos
INSERT INTO mud_being_class(being_class, name, description, size, weight_capacity) VALUES ('CRINOS', 'Werewolf', 'Werewolf in Crinos form', 2, 25);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('CRINOS', 'STR', 12);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('CRINOS', 'DEX', 10);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('CRINOS', 'INT', 6);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('CRINOS', 'CHR', 6);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('CRINOS', 'HP', 15);
INSERT INTO mud_being_class_skill(being_class, skill_code, skill_value) VALUES ('CRINOS', 'BRAWLER', 80);

-- Ox
INSERT INTO mud_being_class(being_class, name, size, weight_capacity) VALUES ('OX', 'Ox', 4, 50);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('OX', 'STR', 15);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('OX', 'DEX', 6);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('OX', 'INT', 6);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('OX', 'CHR', 4);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('OX', 'HP', 20);
