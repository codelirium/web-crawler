package io.codelirium.crawler.spider.impl;

import io.codelirium.crawler.spider.Spider;
import io.codelirium.crawler.spider.annotation.Goliath;
import io.codelirium.crawler.spider.impl.leg.JSSpiderLeg;
import io.codelirium.crawler.spider.impl.leg.LinkSpiderLeg;
import javax.annotation.concurrent.ThreadSafe;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newConcurrentMap;
import static io.codelirium.crawler.spider.impl.leg.JSSpiderLeg.*;
import static io.codelirium.crawler.spider.impl.leg.LinkSpiderLeg.ANCHOR_HREF_ATTRIBUTE_KEY;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.util.Collections.frequency;
import static java.util.Collections.reverseOrder;
import static java.util.Map.Entry.comparingByValue;
import static java.util.Objects.isNull;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.Assert.notNull;


@Goliath
@ThreadSafe
public class GoogleJSSpiderImpl implements Spider {

	private static final String URL_GOOGLE_SEARCH = "https://www.google.de/search?q=";

	// Used for testing only.
	private LinkSpiderLeg testLinkSpiderLeg;
	private JSSpiderLeg   testJSSpiderLeg;


	public GoogleJSSpiderImpl() { }

	// Used for testing only.
	GoogleJSSpiderImpl(final LinkSpiderLeg testLinkSpiderLeg, final JSSpiderLeg testJSSpiderLeg) {

		this.testLinkSpiderLeg = testLinkSpiderLeg;
		this.testJSSpiderLeg   = testJSSpiderLeg;

	}


	@Override
	public void search(final String searchTerm) {

		notNull(searchTerm, "The search term cannot be null.");


		// 1. Get a Google result page from the urlContainsSearchTerm term & 2. Extract main result links from the page.

		final LinkSpiderLeg googleLinkSpiderLeg    = isNull(this.testLinkSpiderLeg) ? new LinkSpiderLeg() : this.testLinkSpiderLeg;
		final List<String> googleSearchResultLinks = googleLinkSpiderLeg
																	.crawl( format("%s%s", URL_GOOGLE_SEARCH, searchTerm),
																			GOOGLE_RESULT_ANCHOR_LINK_CSS_QUERY,
																			ANCHOR_HREF_ATTRIBUTE_KEY);


		// 3. Download the respective pages and extract the names of javascript libraries used in them.

		final List<String>    jsLibraries     = new CopyOnWriteArrayList<>();
		final ExecutorService executorService = newFixedThreadPool(getRuntime().availableProcessors());
		final CountDownLatch  latch           = new CountDownLatch(googleSearchResultLinks.size());

		googleSearchResultLinks.parallelStream().forEach(link -> {

			final JSSpiderLeg jsSpiderLeg = isNull(this.testJSSpiderLeg) ? new JSSpiderLeg() : this.testJSSpiderLeg;

			final Runnable crawlRunnable = () -> {

				jsLibraries.addAll(jsSpiderLeg.crawl(link, SCRIPT_CSS_QUERY, SCRIPT_SRC_ATTRIBUTE_KEY));

				latch.countDown();
			};

			executorService.submit(crawlRunnable);
		});

		while (latch.getCount() != 0) ;

		executorService.shutdown();


		// 4. Print top 5 most used libraries to standard output.

		final Map<String, Integer> frequencyUtilisation = newConcurrentMap();

		jsLibraries.parallelStream().forEach(jsLibrary -> frequencyUtilisation.put(jsLibrary, frequency(jsLibraries, jsLibrary)));


		final Map<String, Integer> sortedFrequencyUtilisation = frequencyUtilisation.entrySet()
																						.stream()
																						.sorted(comparingByValue(reverseOrder()))
																						.collect(toMap( Entry::getKey,
																										Entry::getValue,
																										(e1, e2) -> e1,
																										LinkedHashMap::new));


		final Set<String> topJSLibraries = sortedFrequencyUtilisation.keySet();

		if (topJSLibraries.size() <= 5) {

			topJSLibraries.parallelStream().forEach(System.out::println);

		} else {

			newArrayList(topJSLibraries)
								.subList(0, 5)
								.parallelStream()
								.forEach(System.out::println);

		}
	}
}
