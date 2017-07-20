delete from mud_item_attr;
delete from mud_item;
delete from mud_item_class_attr;
delete from mud_item_class;

-- MATERIAL:
INSERT INTO mud_item_class(item_class, size, weight) VALUES ('SCRAP', 1, 1);
INSERT INTO mud_item_class(item_class, size, weight) VALUES ('METAL', 0.01, 0.01);
INSERT INTO mud_item_class(item_class, size, weight) VALUES ('ROCK', 0.01, 0.01);
INSERT INTO mud_item_class(item_class, size, weight) VALUES ('STONE', 1, 1);
INSERT INTO mud_item_class(item_class, size, weight) VALUES ('BRICK', 0.01, 0.01);
INSERT INTO mud_item_class(item_class, size, weight) VALUES ('TREE', 100, 100);
INSERT INTO mud_item_class(item_class, size, weight) VALUES ('TRUNK', 10, 10);
	
-- FUEL
INSERT INTO mud_item_class(item_class, size, weight) VALUES ('POWUNIT', 1, 1);
	
-- FOOD
INSERT INTO mud_item_class(item_class, size, weight) VALUES ('SEED', 0.01, 0.01);
INSERT INTO mud_item_class(item_class, size, weight) VALUES ('RAWFOOD', 0.01, 0.01);
INSERT INTO mud_item_class(item_class, size, weight) VALUES ('RATION', 0.01, 0.01);
INSERT INTO mud_item_class(item_class, size, weight) VALUES ('PROCFOOD', 0.01, 0.01);
INSERT INTO mud_item_class(item_class, size, weight) VALUES ('PROTEIN', 0.01, 0.01);
	
-- TOOL
INSERT INTO mud_item_class(item_class, size, weight) VALUES ('SCYTHE', 1, 1);
INSERT INTO mud_item_class(item_class, size, weight) VALUES ('DRILL', 1, 1);
	
-- WEAPON
INSERT INTO mud_item_class(item_class, size, weight) VALUES ('LOG', 0.25, 0.25);
INSERT INTO mud_item_class(item_class, size, weight) VALUES ('PISTOL', 0.25, 0.25);


INSERT INTO mud_item_class_attr(item_class, attr_code, attr_value) VALUES ('POWUNIT', 'MAXDUR', 500);
INSERT INTO mud_item_class_attr(item_class, attr_code, attr_value) VALUES ('POWUNIT', 'DUR', 500);
INSERT INTO mud_item_class_attr(item_class, attr_code, attr_value) VALUES ('SCYTHE', 'MAXDUR', 50);
INSERT INTO mud_item_class_attr(item_class, attr_code, attr_value) VALUES ('SCYTHE', 'DUR', 50);
INSERT INTO mud_item_class_attr(item_class, attr_code, attr_value) VALUES ('DRILL', 'MAXDUR', 50);
INSERT INTO mud_item_class_attr(item_class, attr_code, attr_value) VALUES ('DRILL', 'DUR', 50);

INSERT INTO mud_item_class_attr(item_class, attr_code, attr_value) VALUES ('TREE', 'MAXDUR', 500);
INSERT INTO mud_item_class_attr(item_class, attr_code, attr_value) VALUES ('TREE', 'DUR', 500);
INSERT INTO mud_item_class_attr(item_class, attr_code, attr_value) VALUES ('STONE', 'MAXDUR', 500);
INSERT INTO mud_item_class_attr(item_class, attr_code, attr_value) VALUES ('STONE', 'DUR', 500);


INSERT INTO mud_item(item_code, item_class, name, description, current_world, current_place) VALUES (1, 'STONE', 'Stone', 'Stone', 'aforgotten', 1);

INSERT INTO mud_item_attr(item_code, attr_code, attr_value) VALUES (1, 'MAXDUR', 500);
INSERT INTO mud_item_attr(item_code, attr_code, attr_value) VALUES (1, 'DUR', 500);
