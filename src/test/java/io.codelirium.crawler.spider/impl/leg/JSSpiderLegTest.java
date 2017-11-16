package io.codelirium.crawler.spider.impl.leg;

import io.codelirium.crawler.spider.BaseTest;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.io.IOException;

import static com.google.common.collect.Lists.newArrayList;
import static io.codelirium.crawler.spider.impl.leg.JSSpiderLeg.SCRIPT_CSS_QUERY;
import static io.codelirium.crawler.spider.impl.leg.JSSpiderLeg.SCRIPT_SRC_ATTRIBUTE_KEY;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class JSSpiderLegTest extends BaseTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private Connection testConnection;
	@Mock
	private Document testDocument;
	@Mock
	private Element elementOne;
	@Mock
	private Element elementTwo;
	@Mock
	private Element elementThree;

	private JSSpiderLeg jsSpiderLeg;


	@Before
	public void setUp() throws IOException {

		reset(testConnection);

		when(testConnection.get()).thenReturn(testDocument);
		when(testDocument.select(SCRIPT_CSS_QUERY)).thenReturn(new Elements(newArrayList(elementOne, elementTwo, elementThree)));
		when(elementOne.absUrl(SCRIPT_SRC_ATTRIBUTE_KEY)).thenReturn(format("%s/%s", DUMMY_BASE_URL, JS_LIB_1));
		when(elementTwo.absUrl(SCRIPT_SRC_ATTRIBUTE_KEY)).thenReturn(format("%s/%s", DUMMY_BASE_URL, JS_LIB_2));
		when(elementThree.absUrl(SCRIPT_SRC_ATTRIBUTE_KEY)).thenReturn(format("%s/%s", DUMMY_BASE_URL, JS_LIB_3));

		jsSpiderLeg = new JSSpiderLeg(testConnection);
	}

	@Test
	public void testThatJSSpiderLegThrowsExceptionForNullInputOne() throws IOException {

		expectedException.expect(IllegalArgumentException.class);

		jsSpiderLeg.crawl(null, DUMMY_INPUT, DUMMY_INPUT);
	}

	@Test
	public void testThatJSSpiderLegThrowsExceptionForNullInputTwo() throws IOException {

		expectedException.expect(IllegalArgumentException.class);

		jsSpiderLeg.crawl(DUMMY_INPUT, null, DUMMY_INPUT);
	}

	@Test
	public void testThatJSSpiderLegThrowsExceptionForNullInputThree() throws IOException {

		expectedException.expect(IllegalArgumentException.class);

		jsSpiderLeg.crawl(DUMMY_INPUT, DUMMY_INPUT, null);
	}

	@Test
	public void testThatJSSpiderLegCrawlsTheRightJSLibraries() {

		assertThat(jsSpiderLeg.crawl(DUMMY_BASE_URL, SCRIPT_CSS_QUERY, SCRIPT_SRC_ATTRIBUTE_KEY), containsInAnyOrder(JS_LIB_1, JS_LIB_2, JS_LIB_3));

	}
}
