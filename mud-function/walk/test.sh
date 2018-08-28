#!/bin/sh

node-lambda run -j test/successTest.json --handler WALK_prereq.handler -x test/context.json

node-lambda run -j test/noExitTest.json --handler WALK_prereq.handler -x test/context.json

node-lambda run -j test/closedExitTest.json --handler WALK_prereq.handler -x test/context.json
