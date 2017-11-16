package io.codelirium.crawler.spider.impl;

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
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static io.codelirium.crawler.spider.impl.leg.JSSpiderLeg.SCRIPT_CSS_QUERY;
import static io.codelirium.crawler.spider.impl.leg.JSSpiderLeg.SCRIPT_SRC_ATTRIBUTE_KEY;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class GoogleJSSpiderImplTest {

	private static final String DUMMY_SEARCH_TERM = "FOO";

	private static final String LINK_1 = "https://scalable.capital";
	private static final String LINK_2 = "https://www.github.com";
	private static final String LINK_3 = "https://www.bitbucket.com";

	private static final String JS_LIB_1 = "jquery.js";
	private static final String JS_LIB_2 = "vue.js";
	private static final String JS_LIB_3 = "bootstrap.js";
	private static final String JS_LIB_4 = "analytics.js";
	private static final String JS_LIB_5 = "leetskinz.js";
	private static final String JS_LIB_6 = "dummy1.js";
	private static final String JS_LIB_7 = "dummy2.js";
	private static final String JS_LIB_8 = "dummy3.js";


	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

	@Mock
	private LinkSpiderLeg linkSpiderLeg;
	@Mock
	private JSSpiderLeg jsSpiderLeg;

	private GoogleJSSpiderImpl spider;


	@Before
	public void setUp() {

		reset(linkSpiderLeg);

		doReturn(getDummyGoogleSearchResults()).when(linkSpiderLeg).crawl(anyString(), anyString(), anyString());


		reset(jsSpiderLeg);

		when(jsSpiderLeg.crawl(LINK_1, SCRIPT_CSS_QUERY, SCRIPT_SRC_ATTRIBUTE_KEY)).thenReturn(getLink1JSLibrariesWithPaths());
		when(jsSpiderLeg.crawl(LINK_2, SCRIPT_CSS_QUERY, SCRIPT_SRC_ATTRIBUTE_KEY)).thenReturn(getLink2JSLibrariesWithPaths());
		when(jsSpiderLeg.crawl(LINK_3, SCRIPT_CSS_QUERY, SCRIPT_SRC_ATTRIBUTE_KEY)).thenReturn(getLink3JSLibrariesWithPaths());

		spider = new GoogleJSSpiderImpl(linkSpiderLeg, jsSpiderLeg);
	}


	@Test
	public void testThatGoogleJSSpiderThrowsExceptionForNullSearchTerm() throws IOException {

		expectedException.expect(IllegalArgumentException.class);

		spider.search(null);
	}

	@Test
	public void testThatGoogleJSSpiderReturnsTheRightJSLibraries() throws IOException {

		spider.search(DUMMY_SEARCH_TERM);

		assertTrue(systemOutRule.getLog().contains(JS_LIB_1));
		assertTrue(systemOutRule.getLog().contains(JS_LIB_2));
		assertTrue(systemOutRule.getLog().contains(JS_LIB_3));
		assertTrue(systemOutRule.getLog().contains(JS_LIB_4));
		assertTrue(systemOutRule.getLog().contains(JS_LIB_5));
	}


	private List<String> getDummyGoogleSearchResults() {

		return newArrayList(LINK_1, LINK_2, LINK_3);

	}

	private List<String> getLink1JSLibrariesWithPaths() {

		return newArrayList(JS_LIB_1, JS_LIB_2, JS_LIB_3, JS_LIB_4, JS_LIB_5, JS_LIB_6, JS_LIB_7, JS_LIB_8);

	}

	private List<String> getLink2JSLibrariesWithPaths() {

		return newArrayList(JS_LIB_1, JS_LIB_2, JS_LIB_3, JS_LIB_4, JS_LIB_5);

	}

	private List<String> getLink3JSLibrariesWithPaths() {

		return newArrayList(JS_LIB_1, JS_LIB_2, JS_LIB_3, JS_LIB_4, JS_LIB_5);

	}
}
