insert into mud_terrain_category(category_code, name, description) values(1, 'Land', 'Land');
insert into mud_terrain_category(category_code, name, description) values(2, 'Water', 'Water');
insert into mud_terrain_category(category_code, name, description) values(3, 'Air', 'Air');
insert into mud_terrain_category(category_code, name, description) values(4, 'Underground', 'Underground');
insert into mud_terrain_category(category_code, name, description) values(5, 'UnderWater', 'UnderWater');
insert into mud_terrain_category(category_code, name, description) values(6, 'Swamp', 'Swamp');
insert into mud_terrain_category(category_code, name, description) values(7, 'Space', 'Space');

insert into mud_terrain(terrain_code, name, description, category_code, size_capacity, weight_capacity) values(1, 'Plain', 'Terra firme ao ar livre', 1, 900, 900);
insert into mud_terrain(terrain_code, name, description, category_code, size_capacity, weight_capacity) values(2, 'Swamp', 'Swamp', 6, 100, 0);
insert into mud_terrain(terrain_code, name, description, category_code, size_capacity, weight_capacity) values(3, 'Lake', 'Lago', 2, 50, 0);
insert into mud_terrain(terrain_code, name, description, category_code, size_capacity, weight_capacity) values(4, 'UnderWater', 'Lake Bottom', 5, 50, 100);
insert into mud_terrain(terrain_code, name, description, category_code, size_capacity, weight_capacity) values(5, 'River', 'River', 2, 50, 0);
insert into mud_terrain(terrain_code, name, description, category_code, size_capacity, weight_capacity) values(6, 'Tunnel', 'Passagem subterranea', 4, 10, 0);
insert into mud_terrain(terrain_code, name, description, category_code, size_capacity, weight_capacity) values(7, 'Building', 'Predio pequeno', 4, 50, 100);
insert into mud_terrain(terrain_code, name, description, category_code, size_capacity, weight_capacity) values(8, 'BigBuilding', 'Predio grande', 4, 200, 500);

-- being classes

-- Human
INSERT INTO mud_being_class(being_class, name, description, size, weight_capacity) VALUES ('HUMAN', 'Human', 'Average Human being', 1, 10);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('HUMAN', 'STR', 8);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('HUMAN', 'DEX', 8);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('HUMAN', 'INT', 8);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('HUMAN', 'CHR', 8);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('HUMAN', 'HP', 10);
INSERT INTO mud_being_class_skills(being_class, skill_code, skill_value) VALUES ('HUMAN', 'FARMER', 10);
INSERT INTO mud_being_class_skills(being_class, skill_code, skill_value) VALUES ('HUMAN', 'BRAWLER', 50);


-- Crinos
INSERT INTO mud_being_class(being_class, name, description, size, weight_capacity) VALUES ('CRINOS', 'Werewolf', 'Werewolf in Crinos form', 2, 25);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('CRINOS', 'STR', 12);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('CRINOS', 'DEX', 10);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('CRINOS', 'INT', 6);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('CRINOS', 'CHR', 6);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('CRINOS', 'HP', 15);
INSERT INTO mud_being_class_skills(being_class, skill_code, skill_value) VALUES ('CRINOS', 'BRAWLER', 80);

-- Ox
INSERT INTO mud_being_class(being_class, name, size, weight_capacity) VALUES ('OX', 'Ox', 4, 50);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('OX', 'STR', 15);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('OX', 'DEX', 6);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('OX', 'INT', 6);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('OX', 'CHR', 4);
INSERT INTO mud_being_class_attr(being_class, attr_code, attr_value) VALUES ('OX', 'HP', 20);



INSERT INTO mud_place(place_code, name, description, terrain_code) values(1, 'Escape Pod', 'Your escape pod', 7);
INSERT INTO mud_place(place_code, name, description, terrain_code) values(2, 'Crash Site', 'Place where your escape pod landed', 1);
INSERT INTO mud_place(place_code, name, description, terrain_code) values(3, 'Lake', 'A regular lake north of your landing spot', 3);
INSERT INTO mud_place(place_code, name, description, terrain_code) values(4, 'Lake Bottom', 'Bottom of the lake', 4);

INSERT INTO mud_place_exits(place_code, direction, name, opened, visible, target_place_code) values (1, 'OUT', 'Outside the pod', true, true, 2);
INSERT INTO mud_place_exits(place_code, direction, name, opened, visible, target_place_code) values (2, 'NORTH', 'A Lake', true, true, 3);
INSERT INTO mud_place_exits(place_code, direction, name, opened, visible, target_place_code) values (2, 'IN', 'The Escape Pod', true, true, 1);

INSERT INTO mud_place_exits(place_code, direction, name, opened, visible, target_place_code) values (3, 'DOWN', 'Lake bottom', true, true, 4);

INSERT INTO mud_place_exits(place_code, direction, name, opened, visible, target_place_code) values (4, 'UP', 'Surface', true, true, 3);

