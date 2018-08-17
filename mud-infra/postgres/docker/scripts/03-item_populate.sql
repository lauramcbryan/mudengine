set role mudengine_item;

-- MATERIAL:
INSERT INTO mud_item_class(code, name, size, weight) VALUES ('SCRAP', 'Scrap', 1, 1);
INSERT INTO mud_item_class(code, name, size, weight) VALUES ('METAL', 'Metal', 0.01, 0.01);
INSERT INTO mud_item_class(code, name, size, weight) VALUES ('ROCK', 'Rock', 0.01, 0.01);
INSERT INTO mud_item_class(code, name, size, weight) VALUES ('STONE', 'Stone', 1, 1);
INSERT INTO mud_item_class(code, name, size, weight) VALUES ('BRICK', 'Brick', 0.01, 0.01);
INSERT INTO mud_item_class(code, name, size, weight) VALUES ('TREE', 'Tree', 100, 100);
INSERT INTO mud_item_class(code, name, size, weight) VALUES ('TRUNK', 'Trunk', 10, 10);
	
-- FUEL
INSERT INTO mud_item_class(code, name, size, weight) VALUES ('POWUNIT', 'Power Unit', 1, 1);
	
-- FOOD
INSERT INTO mud_item_class(code, name, size, weight) VALUES ('SEED', 'Seed', 0.01, 0.01);
INSERT INTO mud_item_class(code, name, size, weight) VALUES ('RAWFOOD', 'Raw Food', 0.01, 0.01);
INSERT INTO mud_item_class(code, name, size, weight) VALUES ('RATION', 'Ration', 0.01, 0.01);
INSERT INTO mud_item_class(code, name, size, weight) VALUES ('PROCFOOD', 'Processed Food', 0.01, 0.01);
INSERT INTO mud_item_class(code, name, size, weight) VALUES ('PROTEIN', 'Whey Protein', 0.01, 0.01);
	
-- TOOL
INSERT INTO mud_item_class(code, name, size, weight) VALUES ('SCYTHE', 'Scythe', 1, 1);
INSERT INTO mud_item_class(code, name, size, weight) VALUES ('DRILL', 'Drill', 1, 1);
	
-- WEAPON
INSERT INTO mud_item_class(code, name, size, weight) VALUES ('LOG', 'Log', 0.25, 0.25);
INSERT INTO mud_item_class(code, name, size, weight) VALUES ('PISTOL', 'Pistol', 0.25, 0.25);


INSERT INTO mud_item_class_attr(class_code, code, value) VALUES ('POWUNIT', 'MAXDUR', 500);
INSERT INTO mud_item_class_attr(class_code, code, value) VALUES ('POWUNIT', 'DUR', 500);
INSERT INTO mud_item_class_attr(class_code, code, value) VALUES ('SCYTHE', 'MAXDUR', 50);
INSERT INTO mud_item_class_attr(class_code, code, value) VALUES ('SCYTHE', 'DUR', 50);
INSERT INTO mud_item_class_attr(class_code, code, value) VALUES ('DRILL', 'MAXDUR', 50);
INSERT INTO mud_item_class_attr(class_code, code, value) VALUES ('DRILL', 'DUR', 50);

INSERT INTO mud_item_class_attr(class_code, code, value) VALUES ('TREE', 'MAXDUR', 500);
INSERT INTO mud_item_class_attr(class_code, code, value) VALUES ('TREE', 'DUR', 500);
INSERT INTO mud_item_class_attr(class_code, code, value) VALUES ('STONE', 'MAXDUR', 500);
INSERT INTO mud_item_class_attr(class_code, code, value) VALUES ('STONE', 'DUR', 500);


INSERT INTO mud_item(code, class_code, name, current_world, current_place) VALUES (1, 'STONE', 'Dark Stone', 'aforgotten', 2);

INSERT INTO mud_item_attr(item_code, code, value) VALUES (1, 'MAXDUR', 500);
INSERT INTO mud_item_attr(item_code, code, value) VALUES (1, 'DUR', 500);

reset role;