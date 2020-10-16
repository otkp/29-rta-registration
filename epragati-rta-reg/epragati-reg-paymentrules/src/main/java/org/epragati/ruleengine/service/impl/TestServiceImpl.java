package org.epragati.ruleengine.service.impl;

import org.epragati.ruleengine.service.TestService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

	@Override
	public void test() {
		System.out.println("Test for quarterly Tax");
	}

}
