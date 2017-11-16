package io.codelirium.crawler.spider.impl;

import io.codelirium.crawler.spider.BaseTest;
import io.codelirium.crawler.spider.impl.leg.JSSpiderLeg;
import io.codelirium.crawler.spider.impl.leg.LinkSpiderLeg;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.io.IOException;

import static io.codelirium.crawler.spider.impl.leg.JSSpiderLeg.SCRIPT_CSS_QUERY;
import static io.codelirium.crawler.spider.impl.leg.JSSpiderLeg.SCRIPT_SRC_ATTRIBUTE_KEY;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class GoogleJSSpiderImplTest extends BaseTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

	@Mock
	private LinkSpiderLeg testLinkSpiderLeg;
	@Mock
	private JSSpiderLeg testJSSpiderLeg;

	private GoogleJSSpiderImpl spider;


	@Before
	public void setUp() {

		reset(testLinkSpiderLeg);

		doReturn(getDummyGoogleSearchResults()).when(testLinkSpiderLeg).crawl(anyString(), anyString(), anyString());


		reset(testJSSpiderLeg);

		when(testJSSpiderLeg.crawl(LINK_1, SCRIPT_CSS_QUERY, SCRIPT_SRC_ATTRIBUTE_KEY)).thenReturn(getLink1JSLibrariesWithPaths());
		when(testJSSpiderLeg.crawl(LINK_2, SCRIPT_CSS_QUERY, SCRIPT_SRC_ATTRIBUTE_KEY)).thenReturn(getLink2JSLibrariesWithPaths());
		when(testJSSpiderLeg.crawl(LINK_3, SCRIPT_CSS_QUERY, SCRIPT_SRC_ATTRIBUTE_KEY)).thenReturn(getLink3JSLibrariesWithPaths());

		spider = new GoogleJSSpiderImpl(testLinkSpiderLeg, testJSSpiderLeg);
	}


	@Test
	public void testThatGoogleJSSpiderThrowsExceptionForNullSearchTerm() throws IOException {

		expectedException.expect(IllegalArgumentException.class);

		spider.search(null);
	}

	@Test
	public void testThatGoogleJSSpiderReturnsTheRightJSLibraries() throws IOException {

		spider.search(DUMMY_INPUT);

		assertTrue(systemOutRule.getLog().contains(JS_LIB_1));
		assertTrue(systemOutRule.getLog().contains(JS_LIB_2));
		assertTrue(systemOutRule.getLog().contains(JS_LIB_3));
		assertTrue(systemOutRule.getLog().contains(JS_LIB_4));
		assertTrue(systemOutRule.getLog().contains(JS_LIB_5));
	}
}
