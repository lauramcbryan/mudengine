package com.jpinfo.mudengine.common.interfaces;

import java.util.*;

public interface ActionTarget {

	Collection<Reaction> getReactions(String actionCode, boolean isBefore);
	
}
