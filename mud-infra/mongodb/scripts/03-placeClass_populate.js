var db = connect("127.0.0.1:27017/mudenginedb");

db.placeClass.insertMany(
	[
		{
			"_id": "PLAIN",
			"name": "Plain",
			"description": "A plain"
		},
		{
			"_id": "FOREST",
			"name": "Forest",
			"description": "A forest"
		},
		{
			"_id": "LAKE",
			"name": "Lake",
			"description": "A lake"
		},
		{
			"_id": "RIVER",
			"name": "River",
			"description": "The surface"
		},
		{
			"_id": "UWATER",
			"name": "Lake",
			"description": "Under the surface"
		},
		{
			"_id": "SKY",
			"name": "Sky",
			"description": "The sky"
		},
		{
			"_id": "SPACE",
			"name": "Space",
			"description": "The empty space"
		},
		{
			"_id": "RUIN",
			"name": "Ruins",
			"description": "Ruins",
			"sizeCapacity": 0
		},
		{
			"_id": "TUNNEL",
			"name": "Tunnel",
			"description": "A small underground passage",
			"sizeCapacity": 10,
			"demisedClassCode": "RUIN"
		},
		{
			"_id": "BIGTUNNEL",
			"name": "Large Tunnel",
			"description": "A large underground Tunnel",
			"sizeCapacity": 100,
			"demisedClassCode": "TUNNEL"
		},
		{
			"_id": "WORKSHOP",
			"name": "Workshop",
			"description": "A workshop",
			"sizeCapacity": 90,
			"parentClassCode": "BIGTUNNEL",
			"demisedClassCode": "BIGTUNNEL",
			"buildCost": 500,
			"buildEffort": 100
		},
		{
			"_id": "POD",
			"name": "Escape Pod",
			"description": "One of numerous escape pods in the planet",
			"sizeCapacity": 90,
			"demisedClassCode": "RUIN",
			"attrs": [
				{
					"HP": 100
				},
				{
					"MAXHP": 100
				}
			]
		},
		{
			"_id": "LNODE",
			"name": "Living Node",
			"description": "A small habitation node with capacity for 100 people",
			"sizeCapacity": 100,
			"parentClassCode": "BIGTUNNEL",
			"demisedClassCode": "BIGTUNNEL",
			"buildCost": 300,
			"buildEffort": 100
		},
		{
			"_id": "RSTATION",
			"name": "Radio Station",
			"description": "A radio station.  Just capable of hearing other stations, no transmission.",
			"sizeCapacity": 10,
			"parentClassCode": "BIGTUNNEL",
			"demisedClassCode": "BIGTUNNEL",
			"buildCost": 100,
			"buildEffort": 50
		},
		{
			"_id": "LRTRANS",
			"name": "Long Range Transmitter",
			"description": "A long range transmission station.  With such station communications with the entire planet are possible.",
			"sizeCapacity": 15,
			"parentClassCode": "RSTATION",
			"demisedClassCode": "BIGTUNNEL",
			"buildCost": 100,
			"buildEffort": 50
		},
		{
			"_id": "NETWORK",
			"name": "Remote Network",
			"description": "A remote network station.  Capable of high speed transmissions at long distance.",
			"sizeCapacity": 20,
			"parentClassCode": "LRTRANS",
			"demisedClassCode": "BIGTUNNEL",
			"buildCost": 1000,
			"buildEffort": 500
		},
		{
			"_id": "SCOLLECT",
			"name": "Scrap Collector",
			"description": "A scrap collect and processing station.",
			"sizeCapacity": 60,
			"parentClassCode": "BIGTUNNEL",
			"demisedClassCode": "BIGTUNNEL",
			"buildCost": 100,
			"buildEffort": 50
		},
		{
			"_id": "RECYCLER",
			"name": "Recycler",
			"description": "A reciclation station.",
			"sizeCapacity": 60,
			"parentClassCode": "SCOLLECT",
			"demisedClassCode": "BIGTUNNEL",
			"buildCost": 300,
			"buildEffort": 100
		},
		{
			"_id": "FACTORY",
			"name": "Factory",
			"description": "A factory.",
			"sizeCapacity": 80,
			"parentClassCode": "BIGTUNNEL",
			"demisedClassCode": "BIGTUNNEL",
			"buildCost": 1000,
			"buildEffort": 500
		},
		{
			"_id": "FARM",
			"name": "Farm",
			"description": "A farm.",
			"sizeCapacity": 200,
			"parentClassCode": "BIGTUNNEL",
			"demisedClassCode": "BIGTUNNEL",
			"buildCost": 500,
			"buildEffort": 100
		},
		{
			"_id": "FPROCESS",
			"name": "Food Processor",
			"description": "A food processor facility.",
			"sizeCapacity": 100,
			"parentClassCode": "BIGTUNNEL",
			"demisedClassCode": "BIGTUNNEL",
			"buildCost": 500,
			"buildEffort": 100
		},
		{
			"_id": "IFPROCESS",
			"name": "Improved Food Processor",
			"description": "Improved food processor facility.",
			"sizeCapacity": 100,
			"parentClassCode": "FPROCESS",
			"demisedClassCode": "BIGTUNNEL",
			"buildCost": 1000,
			"buildEffort": 500
		},
		{
			"_id": "TCENTER",
			"name": "Training Center",
			"description": "A training center.",
			"sizeCapacity": 80,
			"parentClassCode": "BIGTUNNEL",
			"demisedClassCode": "BIGTUNNEL",
			"buildCost": 1000,
			"buildEffort": 500
		}

	]
);
