package com.jpinfo.mudengine.action.utils;

import com.jpinfo.mudengine.action.dto.ActionInfo;

public class ActionTestResult {

	private ActionInfo testData;
	
	private Object result;

	public ActionTestResult() {
	}

	public ActionInfo getTestData() {
		return testData;
	}

	public void setTestData(ActionInfo testData) {
		this.testData = testData;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
	
}
