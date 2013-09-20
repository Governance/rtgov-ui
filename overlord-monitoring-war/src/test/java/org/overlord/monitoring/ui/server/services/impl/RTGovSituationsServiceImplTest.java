package org.overlord.monitoring.ui.server.services.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.monitoring.ui.client.shared.beans.SituationsFilterBean;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.analytics.situation.Situation.Severity;

public class RTGovSituationsServiceImplTest {

	@Test
	public void testPredicateEmptyFilter() {
		SituationsFilterBean filter=new SituationsFilterBean();
		
		RTGovSituationsServiceImpl.SituationsFilterPredicate predicate=
				new RTGovSituationsServiceImpl.SituationsFilterPredicate(filter);
		
		Situation situation=new Situation();
		
		if (!predicate.evaluate(null, situation)) {
			fail("Empty filter should have passed");
		}
		
	}

	@Test
	public void testPredicateSeverityFilterPass() {
		SituationsFilterBean filter=new SituationsFilterBean();
		filter.setSeverity(Severity.Critical.name());
		
		RTGovSituationsServiceImpl.SituationsFilterPredicate predicate=
				new RTGovSituationsServiceImpl.SituationsFilterPredicate(filter);
		
		Situation situation=new Situation();
		situation.setSeverity(Severity.Critical);
		
		if (!predicate.evaluate(null, situation)) {
			fail("Severity filter should have passed");
		}
		
	}

	@Test
	public void testPredicateSeverityFilterFail() {
		SituationsFilterBean filter=new SituationsFilterBean();
		filter.setSeverity(Severity.Critical.name());
		
		RTGovSituationsServiceImpl.SituationsFilterPredicate predicate=
				new RTGovSituationsServiceImpl.SituationsFilterPredicate(filter);
		
		Situation situation=new Situation();
		situation.setSeverity(Severity.High);
		
		if (predicate.evaluate(null, situation)) {
			fail("Severity filter should have failed");
		}
		
	}

	@Test
	public void testPredicateTypeFilterPass() {
		SituationsFilterBean filter=new SituationsFilterBean();
		filter.setType("TestType");
		
		RTGovSituationsServiceImpl.SituationsFilterPredicate predicate=
				new RTGovSituationsServiceImpl.SituationsFilterPredicate(filter);
		
		Situation situation=new Situation();
		situation.setType("TestType");
		
		if (!predicate.evaluate(null, situation)) {
			fail("Type filter should have passed");
		}
		
	}

	@Test
	public void testPredicateTypeFilterFail() {
		SituationsFilterBean filter=new SituationsFilterBean();
		filter.setType("TestType");
		
		RTGovSituationsServiceImpl.SituationsFilterPredicate predicate=
				new RTGovSituationsServiceImpl.SituationsFilterPredicate(filter);
		
		Situation situation=new Situation();
		situation.setType("NotTestType");
		
		if (predicate.evaluate(null, situation)) {
			fail("Type filter should have failed");
		}
		
	}

	@Test
	public void testPredicateTimestampFromFilterPass() {
		long refTime=System.currentTimeMillis();
		
		SituationsFilterBean filter=new SituationsFilterBean();
		filter.setTimestampFrom(new java.util.Date(refTime));
		
		RTGovSituationsServiceImpl.SituationsFilterPredicate predicate=
				new RTGovSituationsServiceImpl.SituationsFilterPredicate(filter);
		
		Situation situation=new Situation();
		situation.setTimestamp(refTime+100);
		
		if (!predicate.evaluate(null, situation)) {
			fail("TimestampFrom filter should have passed");
		}
		
	}

	@Test
	public void testPredicateTimestampFromFilterFail() {
		long refTime=System.currentTimeMillis();
		
		SituationsFilterBean filter=new SituationsFilterBean();
		filter.setTimestampFrom(new java.util.Date(refTime));
		
		RTGovSituationsServiceImpl.SituationsFilterPredicate predicate=
				new RTGovSituationsServiceImpl.SituationsFilterPredicate(filter);
		
		Situation situation=new Situation();
		situation.setTimestamp(refTime-100);
		
		if (predicate.evaluate(null, situation)) {
			fail("TimestampFrom filter should have failed");
		}
		
	}

	@Test
	public void testPredicateTimestampToFilterPass() {
		long refTime=System.currentTimeMillis();
		
		SituationsFilterBean filter=new SituationsFilterBean();
		filter.setTimestampTo(new java.util.Date(refTime));
		
		RTGovSituationsServiceImpl.SituationsFilterPredicate predicate=
				new RTGovSituationsServiceImpl.SituationsFilterPredicate(filter);
		
		Situation situation=new Situation();
		situation.setTimestamp(refTime-100);
		
		if (!predicate.evaluate(null, situation)) {
			fail("TimestampTo filter should have passed");
		}
		
	}

	@Test
	public void testPredicateTimestampToFilterFail() {
		long refTime=System.currentTimeMillis();
		
		SituationsFilterBean filter=new SituationsFilterBean();
		filter.setTimestampTo(new java.util.Date(refTime));
		
		RTGovSituationsServiceImpl.SituationsFilterPredicate predicate=
				new RTGovSituationsServiceImpl.SituationsFilterPredicate(filter);
		
		Situation situation=new Situation();
		situation.setTimestamp(refTime+100);
		
		if (predicate.evaluate(null, situation)) {
			fail("TimestampTo filter should have failed");
		}
		
	}

}
