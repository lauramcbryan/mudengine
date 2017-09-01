package com.jpinfo.mudengine.action;

import java.util.Optional;
import java.util.function.Predicate;

import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

public class LambdaMatcher<T> extends ArgumentMatcher<T> {
	
	private Predicate<T> matcher;
	private Optional<String> description;
	
	public LambdaMatcher(Predicate<T> predicate) {
		this(predicate, null);
	}
	
	public LambdaMatcher(Predicate<T> predicate, String description) {
		this.matcher = predicate;
		this.description = Optional.ofNullable(description);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean matches(Object item) {
		return matcher.test((T) item);
	}

	@Override
	public void describeTo(Description description) {
		this.description.ifPresent(description::appendText);
	}
	
}