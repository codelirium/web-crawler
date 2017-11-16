package io.codelirium.crawler.spider.impl.leg;

import org.jsoup.Connection;
import org.jsoup.select.Elements;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static java.lang.Runtime.getRuntime;
import static java.lang.System.out;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.springframework.util.Assert.notNull;


@ThreadSafe
public class LinkSpiderLeg extends BaseSpiderLeg {

	public static final String ANCHOR_LINK_CSS_QUERY     = "a[href]";
	public static final String ANCHOR_HREF_ATTRIBUTE_KEY = "href";


	@Override
	public List<String> crawl(final String url, final String cssQuery, final String attributeKey) {

		notNull(url, "The url cannot be null.");
		notNull(cssQuery, "The css query cannot be null.");
		notNull(attributeKey, "The attribute key cannot be null.");


		try {

			final Optional<Connection> optionalConnection = getOptionalConnection(url);

			if (!optionalConnection.isPresent()) {

				return emptyList();

			}


			final Elements linksOnPage = optionalConnection.get().get().select(cssQuery);

			out.printf("-> Found (%d) links.\n", linksOnPage.size());


			final ExecutorService executorService = newFixedThreadPool(getRuntime().availableProcessors());

			final CountDownLatch latch = new CountDownLatch(linksOnPage.size());

			final List<String> links = new CopyOnWriteArrayList<>();

			linksOnPage.parallelStream().forEach(link -> {

				executorService.submit(() -> links.add(link.absUrl(attributeKey)));

				latch.countDown();
			});

			while (latch.getCount() != 0);

			executorService.shutdown();


			return unmodifiableList(links);

		} catch(final IOException ioe) {

			return emptyList();

		}
	}
}
